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
package pixelitor.menus;

import pixelitor.gui.OpenComps;
import pixelitor.gui.View;
import pixelitor.utils.CompActivationListener;

import javax.swing.*;

/**
 * A menu item that is enabled only if there is an open image
 */
public class OpenImageEnabledMenuItem extends JMenuItem implements CompActivationListener {
    public OpenImageEnabledMenuItem(Action a) {
        super(a);
        setEnabled(false);
        OpenComps.addActivationListener(this);
    }

    @Override
    public void allCompsClosed() {
        setEnabled(false);
    }

    @Override
    public void compActivated(View oldView, View newView) {
        setEnabled(true);
    }
}
