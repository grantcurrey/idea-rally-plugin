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

import com.dnat.idea.rally.connector.entity.Iteration;
import com.dnat.idea.rally.connector.entity.Story;
import com.dnat.idea.rally.connector.entity.Task;
import com.dnat.idea.rally.ui.background.LoadIteration;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.RowIcon;
import com.intellij.ui.treeStructure.SimpleTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

public class RallyPanel {
    private JPanel mainPanel;
    private SimpleTree rallyTree;
    private Project project;

    private static final Icon A_COMPLETE = GuiUtil.loadIcon("a-complete.png");
    private static final Icon C_COMPLETE = GuiUtil.loadIcon("c-complete.png");
    private static final Icon D_COMPLETE = GuiUtil.loadIcon("d-complete.png");
    private static final Icon P_COMPLETE = GuiUtil.loadIcon("p-complete.png");

    private static final Icon A_INCOMPLETE = GuiUtil.loadIcon("a-incomplete.png");
    private static final Icon C_INCOMPLETE = GuiUtil.loadIcon("c-incomplete.png");
    private static final Icon D_INCOMPLETE = GuiUtil.loadIcon("d-incomplete.png");
    private static final Icon P_INCOMPLETE = GuiUtil.loadIcon("p-incomplete.png");

    public RallyPanel(Project project) {
        this.project = project;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        rallyTree = new SimpleTree();
        rallyTree.getEmptyText().setText("Retrieving Stories");
        rallyTree.setCellRenderer(new RallyTreeRenderer());
        rallyTree.setName("Story Tree");
        rallyTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));


        new LoadIteration(project, this).queue();

    }

    public void selectIteration(Iteration iteration, List<Story> stories) {
        /*List<Job> jobList = jenkins.getJobs();
        if (jobList.isEmpty()) {
            return;
        }*/


        DefaultTreeModel model = (DefaultTreeModel) rallyTree.getModel();
        final DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        rootNode.removeAllChildren();
        model.nodeStructureChanged(rootNode);
        rallyTree.updateUI();

        for (Story story : stories) {
            DefaultMutableTreeNode storyNode = new DefaultMutableTreeNode(story);
            rootNode.add(storyNode);

            if (story.getTasks().size() > 0) {
                for (Task task : story.getTasks()) {
                    DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(task);
                    storyNode.add(taskNode);
                }
            }
        }

        rootNode.setUserObject(iteration);
        rallyTree.setRootVisible(true);
    }

    private class RallyTreeRenderer extends ColoredTreeCellRenderer {
        @Override
        public void customizeCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            if (node.getUserObject() != null) {
                if (node.getUserObject() instanceof Story) {
                    Story story = (Story) node.getUserObject();
                    append(story.getFormattedId() + "-" + story.getName());
                    String state = story.getScheduleState();
                    setIcon(new CompositeIcon(getIconState(state)));
                } else if (node.getUserObject() instanceof Iteration) {
                    Iteration iteration = (Iteration) node.getUserObject();
                    append(iteration.getName());
                } else if (node.getUserObject() instanceof Task){
                    Task task = (Task) node.getUserObject();
                    append(task.getName());
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
