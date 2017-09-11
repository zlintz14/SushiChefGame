package sushigame.view;

import java.awt.BorderLayout;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import sushigame.model.Belt;
import sushigame.model.BeltEvent;
import sushigame.model.BeltObserver;
import sushigame.model.Chef;
import sushigame.model.SushiGameModel;

public class ScoreboardWidget extends JPanel implements BeltObserver {

	private SushiGameModel game_model;
	private JLabel display;
	private JButton sort_by_balance, sort_by_consumed, sort_by_spoiled;
	private JPanel button_container;
	private String sb_html;
	private Chef[] opponent_chefs, chefs;
	private boolean do_sort_balance, do_sort_consumed, do_sort_spoiled;
	
	
	public ScoreboardWidget(SushiGameModel gm) {
		game_model = gm;
		game_model.getBelt().registerBeltObserver(this);
		sb_html = "";
		do_sort_balance = false;
		do_sort_consumed = false;
		do_sort_spoiled = false;
		display = new JLabel();
		display.setVerticalAlignment(SwingConstants.TOP);
		sort_by_balance = new JButton("Sort Leaders in Scoreboard by balance");
		sort_by_consumed = new JButton("Sort Leaders in Scoreboard by amount consumed");
		sort_by_spoiled = new JButton("Sort Leaders in Scoreboard by amount spoiled");
		button_container = new JPanel();
		button_container.setLayout(new BoxLayout(button_container, BoxLayout.PAGE_AXIS));
		button_container.add(sort_by_balance);
		button_container.add(sort_by_consumed);
		button_container.add(sort_by_spoiled);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(display);
		add(button_container);
		//display.setText(makeScoreboardHTML());
		display.setHorizontalAlignment(SwingConstants.CENTER);
		
		
	}

	private String makeScoreboardHTML() {
		sb_html = "";
		sb_html += "<html>";
		sb_html += "<h1>Scoreboard</h1>";

		// Create an array of all chefs and sort by balance.
		opponent_chefs = game_model.getOpponentChefs();
		chefs = new Chef[opponent_chefs.length+1];
		chefs[0] = game_model.getPlayerChef();
		for (int i=1; i<chefs.length; i++) {
			chefs[i] = opponent_chefs[i-1];
		}
		
		sort_by_balance.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				chefs = new Chef[opponent_chefs.length+1];
				chefs[0] = game_model.getPlayerChef();
				for (int i=1; i<chefs.length; i++) {
					chefs[i] = opponent_chefs[i-1];
				}
				sortHighToLowBalance();
				printHighToLowBalance(chefs);
				do_sort_consumed = false;
				do_sort_spoiled = false;
				do_sort_balance = true;
			}
		});
	
		sort_by_consumed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				chefs[0] = game_model.getPlayerChef();
				for (int i=1; i<chefs.length; i++) {
					chefs[i] = opponent_chefs[i-1];
				}
				sortHighToLowConsumed();
				printHighToLowConsumed(chefs);
				do_sort_consumed = true;
				do_sort_spoiled = false;
				do_sort_balance = false;
				
			}
		});
		
		sort_by_spoiled.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				chefs[0] = game_model.getPlayerChef();
				for (int i=1; i<chefs.length; i++) {
					chefs[i] = opponent_chefs[i-1];
				}
				sortHighToLowSpoiled();
				printHighToLowSpoiled(chefs);
				do_sort_consumed = false;
				do_sort_spoiled = true;
				do_sort_balance = false;
				
			}
		});
				
		Arrays.sort(chefs, new HighToLowBalanceComparator());
		for (Chef c : chefs) {
			sb_html += c.getName() + " ($" + Math.round(c.getBalance()*100.0)/100.0 + ") <br>";
		}	
		
		return sb_html;
	}

	public void refresh() {
		if(do_sort_balance){
			sortHighToLowBalance();
			printHighToLowBalance(chefs);
		}else if(do_sort_consumed){
			sortHighToLowConsumed();
			printHighToLowConsumed(chefs);
		}else if(do_sort_spoiled){
			sortHighToLowSpoiled();
			printHighToLowSpoiled(chefs);
		}else{
			display.setText(makeScoreboardHTML());
		}	
	}
	
	@Override
	public void handleBeltEvent(BeltEvent e) {
		if (e.getType() == BeltEvent.EventType.ROTATE) {
			refresh();
		}		
	}
	
	public void printHighToLowBalance(Chef[] chefs){
		sb_html = "";
		sb_html += "<html>";
		sb_html += "<h1>Scoreboard</h1>";
		for (Chef c : chefs) {
			sb_html += c.getName() + " ($" + Math.round(c.getBalance()*100.0)/100.0 + ") <br>";
		}
		display.setText(sb_html);
		display.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public void printHighToLowConsumed(Chef[] chefs){
		sb_html = "";
		sb_html += "<html>";
		sb_html += "<h1>Scoreboard</h1>";
		for (Chef c : chefs) {
			sb_html += c.getName() + " (" + Math.round(c.getFoodAmountConsumed()*100.0)/100.0 + " "
					+ "of food consumed by customers) <br>";
		}
		display.setText(sb_html);
		display.setHorizontalAlignment(SwingConstants.RIGHT);
	}
	
	public void printHighToLowSpoiled(Chef[] chefs){
		sb_html = "";
		sb_html += "<html>";
		sb_html += "<h1>Scoreboard</h1>";
		for (Chef c : chefs) {
			sb_html += c.getName() + " (" + Math.round(c.getFoodAmountSpoiled()*100.0)/100.0 + " "
					+ "of food spoiled) <br>";
		}
		display.setText(sb_html);
		display.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public void sortHighToLowBalance(){
		Arrays.sort(chefs, new HighToLowBalanceComparator());
	}
	
	public void sortHighToLowConsumed(){
		Arrays.sort(chefs, new HighToLowAmountConsumedComparator());
	}
	
	public void sortHighToLowSpoiled(){
		Arrays.sort(chefs, new HighToLowAmountSpoiledComparator());
	}


}
