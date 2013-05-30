package com.dnat.idea.rally.ui.action

import com.dnat.idea.rally.connector.RallySession
import com.dnat.idea.rally.ui.GuiUtil
import com.dnat.idea.rally.ui.RallyPanel
import com.dnat.idea.rally.ui.background.LoadIteration
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.PopupChooserBuilder
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBList

import javax.swing.*
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

public class SelectIterationAction extends DumbAwareAction implements CustomComponentAction {

    private static final Icon ARROWS_ICON = GuiUtil.loadIcon("/ide/", "statusbar_arrows.png")

    JLabel selectedViewLabel
    JPanel selectViewPanel
    RallyPanel rallyPanel


    public SelectIterationAction(RallyPanel rallyPanel) {
        this.rallyPanel = rallyPanel
        selectViewPanel = new JPanel()
        BoxLayout layout = new BoxLayout(selectViewPanel, BoxLayout.X_AXIS)
        selectViewPanel.setLayout(layout)
        selectedViewLabel = new JLabel()
        JLabel show = new JLabel("")
        show.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2))
        selectViewPanel.add(show)
        selectViewPanel.add(selectedViewLabel)
        selectViewPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 3))
        JLabel iconLabel = new JLabel(ARROWS_ICON)
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2))
        selectViewPanel.add(iconLabel, selectedViewLabel)
        selectViewPanel.addMouseListener(new MyMouseAdapter())
    }

    private JBList buildViewList() {
        JBList viewList = new JBList(RallySession.instance.futureIterations)
        viewList.cellRenderer = new SelectViewRenderer()
        return viewList
    }

    @Override
    public void update(AnActionEvent e) {
        GuiUtil.runInSwingThread({
            def iteration = RallySession.instance.selectedIteration
            if(!iteration){
                selectedViewLabel.setText("No rally connection!")
            } else {
                selectedViewLabel.setText(iteration.name)
            }
        })
    }

    @Override
    public JComponent createCustomComponent(Presentation presentation) {
        return selectViewPanel
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
    }

    private class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            final JBList viewList = buildViewList()
            JBPopup popup = new PopupChooserBuilder(viewList)
                    .setMovable(false)
                    .setCancelKeyEnabled(true)
                    .setItemChoosenCallback({
                RallySession.instance.selectIteration(viewList.selectedValue)
                new LoadIteration(null,rallyPanel).queue()
            })
                    .createPopup()

            if (e != null) {
                popup.show(new RelativePoint(e))
            } else {
                final Dimension dimension = popup.content.preferredSize
                final Point at = new Point(-dimension.width / 2, -dimension.height)
                popup.show(new RelativePoint(selectedViewLabel, at))
            }
        }
    }

    private class SelectViewRenderer extends ColoredListCellRenderer {
        @Override
        protected void customizeCellRenderer(JList list, def value, int index, boolean selected, boolean hasFocus) {
            append(value.name)
        }
    }
}

