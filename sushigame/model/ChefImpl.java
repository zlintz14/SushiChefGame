package sushigame.model;

import java.util.ArrayList;
import java.util.List;

import comp401.sushi.Plate;
import comp401.sushi.RedPlate;
import comp401.sushi.Sushi;
import comp401.sushi.IngredientPortion;
import comp401.sushi.Nigiri.NigiriType;
import comp401.sushi.Plate.Color;
import comp401.sushi.Sashimi.SashimiType;

public class ChefImpl implements Chef, BeltObserver {

	private double balance, food_amount_consumed, food_amount_spoiled;
	private List<HistoricalPlate> plate_history;
	private String name;
	private ChefsBelt belt;
	private boolean already_placed_this_rotation;
	
	public ChefImpl(String name, double starting_balance, ChefsBelt belt) {
		this.name = name;
		this.balance = starting_balance;
		this.belt = belt;
		food_amount_consumed = 0.0;
		belt.registerBeltObserver(this);
		already_placed_this_rotation = false;
		plate_history = new ArrayList<HistoricalPlate>();
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String n) {
		this.name = n;
	}

	@Override
	public HistoricalPlate[] getPlateHistory(int history_length) {
		if (history_length < 1 || (plate_history.size() == 0)) {
			return new HistoricalPlate[0];
		}

		if (history_length > plate_history.size()) {
			history_length = plate_history.size();
		}
		return plate_history.subList(plate_history.size()-history_length, plate_history.size()-1).toArray(new HistoricalPlate[history_length]);
	}

	@Override
	public HistoricalPlate[] getPlateHistory() {
		return getPlateHistory(plate_history.size());
	}

	@Override
	public double getBalance() {
		return balance;
	}
	
	@Override
	public double getFoodAmountConsumed(){
		return food_amount_consumed;
	}
	
	@Override
	public double getFoodAmountSpoiled(){
		return food_amount_spoiled;
	}

	@Override
	public void makeAndPlacePlate(Plate plate, int position) 
			throws InsufficientBalanceException, BeltFullException, AlreadyPlacedThisRotationException {

		if (already_placed_this_rotation) {
			throw new AlreadyPlacedThisRotationException();
		}
		
		if (plate.getContents().getCost() > balance) {
			throw new InsufficientBalanceException();
		}
		belt.setPlateNearestToPosition(plate, position);
		balance = balance - plate.getContents().getCost();
		already_placed_this_rotation = true;
	}

	@Override
	public void handleBeltEvent(BeltEvent e) {
		if (e.getType() == BeltEvent.EventType.PLATE_CONSUMED) {
			Plate plate = ((PlateEvent) e).getPlate();
			if (plate.getChef() == this) {
				balance += plate.getPrice();
				Customer consumer = belt.getCustomerAtPosition(((PlateEvent) e).getPosition());
				IngredientPortion[] temp_ingreds = plate.getContents().getIngredients();
				for(int i = 0; i < temp_ingreds.length; i++){
					food_amount_consumed += temp_ingreds[i].getAmount();
				}
				plate_history.add(new HistoricalPlateImpl(plate, consumer));
			}
		} else if (e.getType() == BeltEvent.EventType.PLATE_SPOILED) {
			Plate plate = ((PlateEvent) e).getPlate();
			if(plate.getChef() == this){
				IngredientPortion[] temp_ingreds = plate.getContents().getIngredients();
				for(int i = 0; i < temp_ingreds.length; i++){
					food_amount_spoiled += temp_ingreds[i].getAmount();
				}
				plate_history.add(new HistoricalPlateImpl(plate, null));
			}	
		} else if (e.getType() == BeltEvent.EventType.ROTATE) {
			already_placed_this_rotation = false;
		}
	}
	
	@Override
	public boolean alreadyPlacedThisRotation() {
		return already_placed_this_rotation;
	}
}
