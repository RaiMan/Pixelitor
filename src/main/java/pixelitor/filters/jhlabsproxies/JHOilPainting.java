/*
 * Copyright 2019 Laszlo Balazs-Csiki and Contributors
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor. If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.filters.jhlabsproxies;

import com.jhlabs.image.OilFilter;
import pixelitor.filters.ParametrizedFilter;
import pixelitor.filters.ResizingFilterHelper;
import pixelitor.filters.gui.GroupedRangeParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.IntChoiceParam.Value;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.gui.ShowOriginal;
import pixelitor.utils.ProgressTracker;
import pixelitor.utils.StatusBarProgressTracker;

import java.awt.image.BufferedImage;

import static pixelitor.filters.ResizingFilterHelper.ScaleUpQuality;
import static pixelitor.filters.gui.RandomizePolicy.IGNORE_RANDOMIZE;

/**
 * Oil Painting filter based on the JHLabs OilFilter
 */
public class JHOilPainting extends ParametrizedFilter {
    public static final String NAME = "Oil Painting";

    private static final int FASTER = 0;
    private static final int BETTER = 1;

    private final GroupedRangeParam brushSize = new GroupedRangeParam(
            "Brush Size", 0, 1, 10, false);
    private final RangeParam coarseness = new RangeParam(
            "Coarseness", 2, 25, 255);
    private final IntChoiceParam detailQuality = new IntChoiceParam("Detail Quality",
            new Value[]{
                    new Value("Faster", FASTER),
                    new Value("Better", BETTER),
            }, IGNORE_RANDOMIZE);

    public JHOilPainting() {
        super(ShowOriginal.YES);

        setParams(
                brushSize.withAdjustedRange(0.04),
                coarseness,
                detailQuality
        );
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        int brushX = brushSize.getValue(0);
        int brushY = brushSize.getValue(1);
        if (brushX == 0 && brushY == 0) {
            return src;
        }

        // important to re-create because the progress tracker
        // is different for big and small images
        OilFilter filter = new OilFilter(NAME);

        filter.setLevels(coarseness.getValue());

        ResizingFilterHelper r = new ResizingFilterHelper(src);
        if (r.shouldResize()) {
            ScaleUpQuality scaleUpQuality;
            if (detailQuality.getValue() == BETTER) {
                scaleUpQuality = ScaleUpQuality.BILINEAR11;
            } else if (detailQuality.getValue() == FASTER) {
                scaleUpQuality = ScaleUpQuality.BILINEAR_FAST;
            } else {
                throw new IllegalStateException("value = " + detailQuality.getValue());
            }

            double resizeFactor = r.getResizeFactor();
            // these will determine the real running time of the filter
            int downScaledBrushX = (int) (brushX / resizeFactor);
            int downScaledBrushY = (int) (brushY / resizeFactor);

            int resizeUnits = r.getResizeWorkUnits(scaleUpQuality);
            long filterWorkAmount = (long) downScaledBrushX * downScaledBrushY;
            int filterUnits = (int) (filterWorkAmount / 4);
            int workUnits = resizeUnits + filterUnits;

            ProgressTracker pt = new StatusBarProgressTracker(NAME, workUnits);
            ProgressTracker filterTracker = r.createFilterTracker(pt, filterUnits);

            filter.setProgressTracker(filterTracker);

            filter.setRangeX(downScaledBrushX);
            filter.setRangeY(downScaledBrushY);

            dest = r.invoke(scaleUpQuality, filter, pt, 0);
            pt.finished();
        } else {
            // normal case, no resizing
            filter.setRangeX(brushX);
            filter.setRangeY(brushY);
            dest = filter.filter(src, dest);
        }

        return dest;
    }

    @Override
    public boolean excludedFromAnimation() {
        return true;
    }
}