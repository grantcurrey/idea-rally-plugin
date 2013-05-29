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

import com.dnat.idea.rally.connector.RallySettings;

import javax.swing.*;

public class RallySettingsPanel {
    JPanel mainPanel;

    private JTextField txtServerUrl;
    private JTextField txtUsername;
    private JTextField txtPassword;

    public void loadConfigurationData(RallySettings rallySettings){
        txtServerUrl.setText(rallySettings.getUrl());
        txtUsername.setText(rallySettings.getUsername());
        txtPassword.setText(rallySettings.getPassword());
    }

    public void applyConfigurationData(RallySettings rallySettings){
        rallySettings.setUrl(txtServerUrl.getText());
        rallySettings.setUsername(txtUsername.getText());
        rallySettings.setPassword(txtPassword.getText());
    }

}
