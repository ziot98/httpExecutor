package app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import app.executor.RedirectProcessExecutor;
import app.http.HttpOption;
import app.ui.frame.MainFrame;
import app.ui.frame.OptionFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Starter {

	public static void main(String[] args) {
		
        SwingUtilities.invokeLater(new Runnable() {
			
        	@Override
			public void run() {
				// TODO Auto-generated method stub
				init();
			}
		});
	}
	
    private static void init() {
    	HttpOption option = new HttpOption();
    	RedirectProcessExecutor client = new RedirectProcessExecutor();
    	MainFrame mainFrame = new MainFrame(client, option);
    }
}
