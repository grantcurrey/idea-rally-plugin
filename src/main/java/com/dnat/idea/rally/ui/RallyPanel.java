////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2012, Wotif.com. All rights reserved.
//
// This is unpublished proprietary source code of Wotif.com.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
////////////////////////////////////////////////////////////////////////////////
package com.dnat.idea.rally.ui;

import com.dnat.idea.rally.ui.background.LoadIteration;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.RowIcon;
import com.intellij.ui.treeStructure.SimpleTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.Map;

public class RallyPanel {
    private JPanel mainPanel;
    private SimpleTree rallyTree;

    private static final Icon A_COMPLETE = GuiUtil.loadIcon("a-complete.png");
    private static final Icon C_COMPLETE = GuiUtil.loadIcon("c-complete.png");
    private static final Icon D_COMPLETE = GuiUtil.loadIcon("d-complete.png");
    private static final Icon P_COMPLETE = GuiUtil.loadIcon("p-complete.png");

    private static final Icon A_INCOMPLETE = GuiUtil.loadIcon("a-incomplete.png");
    private static final Icon C_INCOMPLETE = GuiUtil.loadIcon("c-incomplete.png");
    private static final Icon D_INCOMPLETE = GuiUtil.loadIcon("d-incomplete.png");
    private static final Icon P_INCOMPLETE = GuiUtil.loadIcon("p-incomplete.png");


    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        rallyTree = new SimpleTree();
        rallyTree.getEmptyText().setText("Retrieving Stories");
        rallyTree.setCellRenderer(new RallyTreeRenderer());
        rallyTree.setName("Story Tree");
        rallyTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));


        new LoadIteration(null, this).queue();

    }

    public void selectIteration(Map iteration, List<Map> stories) {
        /*List<Job> jobList = jenkins.getJobs();
        if (jobList.isEmpty()) {
            return;
        }*/


        DefaultTreeModel model = (DefaultTreeModel) rallyTree.getModel();
        final DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        rootNode.removeAllChildren();
        model.nodeStructureChanged(rootNode);
        rallyTree.updateUI();

        for (Map story : stories) {
            DefaultMutableTreeNode jobNode = new DefaultMutableTreeNode(story);
            rootNode.add(jobNode);
        }

        rootNode.setUserObject(iteration);
        rallyTree.setRootVisible(true);
    }

    private class RallyTreeRenderer extends ColoredTreeCellRenderer {
        @Override
        public void customizeCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            Map data = (Map) node.getUserObject();

            if (data != null) {

                if (data.get("type") == "story") {
                    append(data.get("formattedId") + "-" + data.get("name"));
                    String state = (String) data.get("scheduleState");
                    setIcon(new CompositeIcon(getIconState(state)));
                } else if (data.get("type") == "iteration") {
                    append((String) data.get("name"));
                }
            }
        }

        public Icon[] getIconState(String state) {

            Icon[] icons = {D_COMPLETE, P_INCOMPLETE, C_INCOMPLETE, A_INCOMPLETE};

            if (state.equals("In-Progress")) {
                icons[1] = P_COMPLETE;
            } else if (state.equals("Completed")) {
                icons[1] = P_COMPLETE;
                icons[2] = C_COMPLETE;
            } else if (state.equals("Accepted")) {
                icons[1] = P_COMPLETE;
                icons[2] = C_COMPLETE;
                icons[3] = A_COMPLETE;
            }
            return icons;
        }
    }

    private static class CompositeIcon extends RowIcon {
        public CompositeIcon(Icon... icons) {
            super(icons.length);
            for (int i = 0; i < icons.length; i++) {
                Icon icon = icons[i];
                setIcon(icon, i);
            }
        }
    }
}
