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

package pixelitor.utils.debug;

import pixelitor.Canvas;
import pixelitor.Composition;
import pixelitor.gui.ImageFrame;
import pixelitor.gui.View;
import pixelitor.gui.ViewContainer;

/**
 * A debugging node for a {@link View}
 */
public class ViewNode extends DebugNode {
    public ViewNode(String name, View view) {
        super(name, view);

        Composition comp = view.getComp();
        add(new CompositionNode(comp));

        addQuotedString("name", comp.getName());

        addQuotedString("mask view mode", view.getMaskViewMode().toString());

        int width = view.getWidth();
        addInt("view width", width);
        int height = view.getHeight();
        addInt("view height", height);

        ViewContainer viewContainer = view.getViewContainer();
        if (viewContainer instanceof ImageFrame) {
            ImageFrame frame = (ImageFrame) viewContainer;
            int frameWidth = frame.getWidth();
            addInt("frameWidth", frameWidth);
            int frameHeight = frame.getHeight();
            addInt("frameHeight", frameHeight);
        }

        addString("zoom level", view.getZoomLevel().toString());
        Canvas canvas = view.getCanvas();
        int zoomedCanvasWidth = canvas.getCoWidth();
        addInt("zoomedCanvasWidth", zoomedCanvasWidth);
        int zoomedCanvasHeight = canvas.getCoHeight();
        addInt("zoomedCanvasHeight", zoomedCanvasHeight);
//        boolean bigCanvas = view.isBigCanvas();
//        addBooleanChild("bigCanvas", bigCanvas);
//        boolean optimizedDrawingEnabled = view.getViewContainer().isOptimizedDrawingEnabled();
//        addBoolean("optimizedDrawingEnabled", optimizedDrawingEnabled);
    }
}
