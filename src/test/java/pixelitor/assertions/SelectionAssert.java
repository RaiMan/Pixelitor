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

package pixelitor.assertions;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.util.Objects;
import pixelitor.selection.Selection;

import java.awt.geom.Rectangle2D;

/**
 * Custom AssertJ assertions for {@link Selection} objects.
 * Based partially on the code generated by CustomAssertionGenerator.
 */
public class SelectionAssert extends AbstractObjectAssert<SelectionAssert, Selection> {
    /**
     * Creates a new <code>{@link SelectionAssert}</code> to make assertions on actual Selection.
     *
     * @param actual the Selection we want to make assertions on.
     */
    public SelectionAssert(Selection actual) {
        super(actual, SelectionAssert.class);
    }

    public SelectionAssert isAlive() {
        isNotNull();

        if (!actual.isAlive()) {
            failWithMessage("\nExpecting that actual Selection is alive but is not.");
        }

        return this;
    }

    public SelectionAssert isNotAlive() {
        isNotNull();

        if (actual.isAlive()) {
            failWithMessage("\nExpecting that actual Selection is not alive but is.");
        }

        return this;
    }

    public SelectionAssert isFrozen() {
        isNotNull();

        if (!actual.isFrozen()) {
            failWithMessage("\nExpecting that actual Selection is frozen but is not.");
        }

        return this;
    }

    public SelectionAssert isNotFrozen() {
        isNotNull();

        if (actual.isFrozen()) {
            failWithMessage("\nExpecting that actual Selection is not frozen but is.");
        }

        return this;
    }

    public SelectionAssert isHidden() {
        isNotNull();

        if (!actual.isHidden()) {
            failWithMessage("\nExpecting that actual Selection is hidden but is not.");
        }

        return this;
    }

    public SelectionAssert isNotHidden() {
        isNotNull();

        if (actual.isHidden()) {
            failWithMessage("\nExpecting that actual Selection is not hidden but is.");
        }

        return this;
    }

    public SelectionAssert isMarching() {
        isNotNull();

        if (!actual.isMarching()) {
            failWithMessage("\nExpecting that actual Selection is marching but is not.");
        }

        return this;
    }

    public SelectionAssert isNotMarching() {
        isNotNull();

        if (actual.isMarching()) {
            failWithMessage("\nExpecting that actual Selection is not marching but is.");
        }

        return this;
    }

    public SelectionAssert hasShape(java.awt.Shape shape) {
        isNotNull();

        String msg = "\nExpecting shape of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

        java.awt.Shape actualShape = actual.getShape();
        if (!Objects.areEqual(actualShape, shape)) {
            failWithMessage(msg, actual, shape, actualShape);
        }

        return this;
    }

    public SelectionAssert hasShapeBounds(Rectangle2D shapeBounds) {
        isNotNull();

        String msg = "\nExpecting shapeBounds of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

        Rectangle2D actualShapeBounds = actual.getShapeBounds2D();
        if (!Objects.areEqual(actualShapeBounds, shapeBounds)) {
            failWithMessage(msg, actual, shapeBounds, actualShapeBounds);
        }

        return this;
    }
}
