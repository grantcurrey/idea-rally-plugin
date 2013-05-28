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

import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.treeStructure.SimpleTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class RallyPanel extends JPanel {
    private JPanel mainPanel;
    private SimpleTree rallyTree;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        rallyTree = new SimpleTree();
        rallyTree.getEmptyText().setText("Retrieving Stories");
        rallyTree.setCellRenderer(new RallyTreeRenderer());
        rallyTree.setName("Story Tree");
        rallyTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
    }

    private class RallyTreeRenderer extends ColoredTreeCellRenderer {
        @Override
        public void customizeCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        }
    }
}
