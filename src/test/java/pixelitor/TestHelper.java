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

package pixelitor;

import org.mockito.MockingDetails;
import pixelitor.colors.FgBgColorSelector;
import pixelitor.colors.FgBgColors;
import pixelitor.filters.Invert;
import pixelitor.filters.painters.TextSettings;
import pixelitor.gui.OpenComps;
import pixelitor.gui.View;
import pixelitor.history.History;
import pixelitor.layers.AdjustmentLayer;
import pixelitor.layers.ContentLayer;
import pixelitor.layers.ImageLayer;
import pixelitor.layers.Layer;
import pixelitor.layers.TextLayer;
import pixelitor.selection.Selection;
import pixelitor.testutils.WithTranslation;
import pixelitor.tools.Alt;
import pixelitor.tools.Ctrl;
import pixelitor.tools.MouseButton;
import pixelitor.tools.Shift;
import pixelitor.tools.Tools;
import pixelitor.tools.util.PMouseEvent;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import static java.awt.event.MouseEvent.MOUSE_DRAGGED;
import static java.awt.event.MouseEvent.MOUSE_MOVED;
import static java.awt.event.MouseEvent.MOUSE_PRESSED;
import static java.awt.event.MouseEvent.MOUSE_RELEASED;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;
import static pixelitor.assertions.PixelitorAssertions.assertThat;
import static pixelitor.layers.LayerMaskAddType.REVEAL_ALL;
import static pixelitor.layers.MaskViewMode.NORMAL;

public class TestHelper {
    public static final int TEST_WIDTH = 20;
    public static final int TEST_HEIGHT = 10;

    private TestHelper() {
    }

    public static void setupMockFgBgSelector() {
        FgBgColorSelector fgBgColorSelector = mock(FgBgColorSelector.class);
        when(fgBgColorSelector.getFgColor()).thenReturn(Color.BLACK);
        when(fgBgColorSelector.getBgColor()).thenReturn(Color.WHITE);
        FgBgColors.setSelector(fgBgColorSelector);
    }

    public static ImageLayer createImageLayer(String layerName, Composition comp) {
        BufferedImage image = createImage();
        ImageLayer layer = new ImageLayer(comp, image, layerName, null);

        return layer;
    }

    public static TextLayer createTextLayer(Composition comp, String name) {
        TextLayer textLayer = new TextLayer(comp, name);
        textLayer.setSettings(TextSettings.createRandomSettings(new Random()));
        return textLayer;
    }

    public static Composition createEmptyComposition() {
        return createEmptyComposition(TEST_WIDTH, TEST_HEIGHT);
    }

    public static Composition createEmptyComposition(int width, int height) {
        Composition comp = Composition.createEmpty(width, height);
        comp.setName("Test");
        setupMockViewFor(comp);

        return comp;
    }

    public static Composition createMockComposition() {
        Composition comp = mock(Composition.class);

        Canvas canvas = new Canvas(TEST_WIDTH, TEST_HEIGHT);
        when(comp.getCanvas()).thenReturn(canvas);
        when(comp.getCanvasImBounds()).thenReturn(
                new Rectangle(0, 0, TEST_WIDTH, TEST_HEIGHT));
        when(comp.getCanvasImWidth()).thenReturn(TEST_WIDTH);
        when(comp.getCanvasImHeight()).thenReturn(TEST_HEIGHT);

        View view = createMockViewWithoutComp();
        when(view.getComp()).thenReturn(comp);
        when(view.getCanvas()).thenReturn(canvas);
        canvas.setView(view);

        when(comp.getView()).thenReturn(view);

        when(comp.getSelection()).thenReturn(null);

        return comp;
    }

    public static Composition create2LayerComposition(boolean addMasks) {
        Composition c = createEmptyComposition();

        ImageLayer layer1 = createImageLayer("layer 1", c);
        ImageLayer layer2 = createImageLayer("layer 2", c);

        c.addLayerInInitMode(layer1);
        c.addLayerInInitMode(layer2);

        if (addMasks) {
            layer1.addMask(REVEAL_ALL);
            layer2.addMask(REVEAL_ALL);
        }

        NORMAL.activate(layer2, "test");

        assert layer2 == c.getActiveLayer();
        assert layer1 == c.getLayer(0);
        assert layer2 == c.getLayer(1);

        c.setDirty(false);

        return c;
    }

    public static BufferedImage createImage() {
        return new BufferedImage(TEST_WIDTH, TEST_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }

    public static Graphics2D createGraphics() {
        return createImage().createGraphics();
    }

    public static Layer createLayerOfClass(Class layerClass, Composition comp) {
        Layer layer;
        if (layerClass.equals(ImageLayer.class)) {
            layer = ImageLayer.createEmpty(comp, "layer 1");
        } else if (layerClass.equals(TextLayer.class)) {
            layer = createTextLayer(comp, "layer 1");
        } else if (layerClass.equals(AdjustmentLayer.class)) {
            layer = new AdjustmentLayer(comp, "layer 1", new Invert());
        } else {
            throw new IllegalStateException();
        }
        return layer;
    }

    public static PMouseEvent createPEvent(int x, int y, int id,
                                           View view) {
        return createPEvent(x, y, id, Ctrl.NO, Alt.NO, Shift.NO, MouseButton.LEFT, view);
    }

    public static PMouseEvent createPEvent(int x, int y, int id,
                                           Ctrl ctrl, Alt alt, Shift shift,
                                           MouseButton mouseButton, View view) {
        MouseEvent e = createEvent(x, y, id, ctrl, alt, shift, mouseButton, view);
        return new PMouseEvent(e, view);
    }

    public static MouseEvent createEvent(int x, int y, int id,
                                         Ctrl ctrl, Alt alt, Shift shift,
                                         MouseButton mouseButton, View view) {
        int modifiers = 0;
        modifiers = ctrl.modify(modifiers);
        modifiers = alt.modify(modifiers);
        modifiers = shift.modify(modifiers);
        modifiers = mouseButton.modify(modifiers);
        boolean popupTrigger = false;
        if (mouseButton == MouseButton.RIGHT) {
            popupTrigger = true;
        }
        //noinspection MagicConstant
        return new MouseEvent(view,
                id,
                System.currentTimeMillis(),
                modifiers,
                x,
                y,
                1, // click count
                popupTrigger
        );
    }

    public static View setupMockViewFor(Composition comp) {
        View view = createMockViewWithoutComp();

        when(view.getComp()).thenReturn(comp);
        when(view.activeIsDrawable()).thenAnswer(
                invocation -> comp.activeIsDrawable());

        comp.setView(view);

        // set it to active only after the comp is set
        // because the active view should return non-null in view.getComp()
        OpenComps.setActiveView(view, false);

        return view;
    }

    public static View createMockViewWithoutComp() {
        View view = mock(View.class);

        when(view.componentToImageSpace(any(Point2D.class))).then(returnsFirstArg());
        when(view.componentToImageSpace(any(Rectangle2D.class))).then(returnsFirstArg());

        // can't just return the argument because this method returns a
        // Rectangle (subclass) from a Rectangle2D (superclass)
        when(view.imageToComponentSpace(any(Rectangle2D.class))).thenAnswer(invocation -> {
            Rectangle2D in = invocation.getArgument(0);
            return new Rectangle(
                    (int) in.getX(), (int) in.getY(),
                    (int) in.getWidth(), (int) in.getHeight());
        });

        when(view.componentXToImageSpace(anyDouble())).then(returnsFirstArg());
        when(view.componentYToImageSpace(anyDouble())).then(returnsFirstArg());
        when(view.imageXToComponentSpace(anyDouble())).then(returnsFirstArg());
        when(view.imageYToComponentSpace(anyDouble())).then(returnsFirstArg());
        when(view.getScaling()).thenReturn(1.0);
        
        Point fakeLocationOnScreen = new Point(0, 0);
        when(view.getLocationOnScreen()).thenReturn(fakeLocationOnScreen);

        Cursor cursor = Cursor.getDefaultCursor();
        when(view.getCursor()).thenReturn(cursor);

        JViewport parent = new JViewport();
        when(view.getParent()).thenReturn(parent);

        when(view.isMock()).thenReturn(true);
        when(view.getMaskViewMode()).thenReturn(NORMAL);

        return view;
    }

    public static void addSelectionRectTo(Composition comp,
                                          int x, int y, int width, int height) {
        Rectangle shape = new Rectangle(x, y, width, height);
        MockingDetails mockingDetails = mockingDetails(comp);
        if (mockingDetails.isMock()) {
            Selection selection = new Selection(shape, comp.getView());
            when(comp.getSelection()).thenReturn(selection);
            when(comp.hasSelection()).thenReturn(true);
        } else {
            comp.createSelectionFromShape(shape);
        }
    }

    public static void moveLayer(Composition comp,
                                 boolean makeDuplicateLayer, int relX, int relY) {
        comp.startMovement(makeDuplicateLayer);
        comp.moveActiveContentRelative(relX, relY);
        comp.endMovement();
    }

    public static void addRectangleSelection(Composition comp, Rectangle rect) {
        comp.setSelectionRef(new Selection(rect, comp.getView()));
    }

    public static void setStandardTestTranslationToAllLayers(Composition comp,
                                                             WithTranslation translation) {
        comp.forEachContentLayer(contentLayer -> {
            // should be used on layers without translation
            int tx = contentLayer.getTX();
            assert tx == 0 : "tx = " + tx + " on " + contentLayer.getName();
            int ty = contentLayer.getTY();
            assert ty == 0 : "ty = " + ty + " on " + contentLayer.getName();

            setStandardTestTranslation(comp, contentLayer, translation);
        });
    }

    public static void setStandardTestTranslation(Composition comp,
                                                  ContentLayer layer,
                                                  WithTranslation translation) {
        // Composition only allows to move the active layer
        // so if the given layer is not active, we need to activate it temporarily
        Layer activeLayerBefore = comp.getActiveLayer();
        boolean activeLayerChanged = false;
        if (layer != activeLayerBefore) {
            comp.setActiveLayer(layer);
            activeLayerChanged = true;
        }

        assertThat(layer).translationIs(0, 0);

        translation.moveLayer(comp);

        int expectedTX = translation.getExpectedTX();
        int expectedTY = translation.getExpectedTY();
        assertThat(layer).translationIs(expectedTX, expectedTY);

        if (activeLayerChanged) {
            comp.setActiveLayer(activeLayerBefore);
        }
    }

    public static void press(int x, int y, View view) {
        press(x, y, Ctrl.NO, Alt.NO, Shift.NO, view);
    }

    public static void press(int x, int y,
                             Ctrl ctrl, Alt alt, Shift shift, View view) {
        MouseEvent e = createEvent(x, y, MOUSE_PRESSED,
            ctrl, alt, shift, MouseButton.LEFT, view);
        Tools.EventDispatcher.mousePressed(e, view);
    }

    public static void drag(int x, int y, View view) {
        drag(x, y, Ctrl.NO, Alt.NO, Shift.NO, view);
    }

    public static void drag(int x, int y,
                            Ctrl ctrl, Alt alt, Shift shift, View view) {
        MouseEvent e = createEvent(x, y, MOUSE_DRAGGED,
            ctrl, alt, shift, MouseButton.LEFT, view);
        Tools.EventDispatcher.mouseDragged(e, view);
    }

    public static void release(int x, int y, View view) {
        release(x, y, Ctrl.NO, Alt.NO, Shift.NO, view);
    }

    public static void release(int x, int y,
                               Ctrl ctrl, Alt alt, Shift shift, View view) {
        MouseEvent e = createEvent(x, y, MOUSE_RELEASED,
            ctrl, alt, shift, MouseButton.LEFT, view);
        Tools.EventDispatcher.mouseReleased(e, view);
    }

    public static void move(int x, int y, View view) {
        move(x, y, Ctrl.NO, Alt.NO, Shift.NO, view);
    }

    public static void move(int x, int y,
                            Ctrl ctrl, Alt alt, Shift shift, View view) {
        MouseEvent e = createEvent(x, y, MOUSE_MOVED,
            ctrl, alt, shift, MouseButton.LEFT, view);
        Tools.EventDispatcher.mouseMoved(e, view);
    }

    public static void assertHistoryEditsAre(String... values) {
        List<String> edits = History.getEditNames();
        assertThat(edits).containsExactly(values);
    }
}
