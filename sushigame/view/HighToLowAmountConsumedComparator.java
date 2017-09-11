package sushigame.view;

import java.util.Comparator;

import sushigame.model.Chef;

public class HighToLowAmountConsumedComparator implements Comparator<Chef> {

	@Override
	public int compare(Chef a, Chef b) {
		return (int) (Math.round(b.getFoodAmountConsumed()*100.0) - 
				Math.round(a.getFoodAmountConsumed()*100));
	}

}
