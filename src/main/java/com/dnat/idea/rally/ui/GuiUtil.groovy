package com.dnat.idea.rally.ui

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.IconLoader

import javax.swing.*

class GuiUtil {
    private static final String ICON_FOLDER = "/images/"

    private GuiUtil() {
    }


    public static void installActionGroupInToolBar(ActionGroup actionGroup, SimpleToolWindowPanel toolWindowPanel, String toolBarName) {
        JComponent actionToolbar = ActionManager.getInstance().createActionToolbar(toolBarName, actionGroup, true).getComponent()
        toolWindowPanel.setToolbar(actionToolbar)
    }

    public static Icon loadIcon(String iconFilename) {
        return IconLoader.findIcon(ICON_FOLDER + iconFilename,GuiUtil.class)
    }


    public static Icon loadIcon(String parentPath, String iconFilename) {
        return IconLoader.findIcon(parentPath + iconFilename,GuiUtil.class)
    }


    public static boolean isUnderDarcula() {
        return UIManager.getLookAndFeel().getName().contains("Darcula")
    }

    public static URL getIconResource(String iconFilename) {
        return GuiUtil.class.getResource(ICON_FOLDER + iconFilename)
    }
}
