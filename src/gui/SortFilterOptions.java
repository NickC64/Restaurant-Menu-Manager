package gui;

import java.util.ArrayList;

import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;

/*
 * SortFilterOptions manages the name and sort keys of the sort by combobox
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

enum SortFilterOptions {

	NONE("None"),
	NAME_ASCENDING("Name: A-Z"),
	NAME_DESCENDING("Name: Z-A"),
	PRICE_ASCENDING("Price: Lowest"),
	PRICE_DESCENDING("Price: Highest"),
	AVAILABLE_YES("Available: Yes"),
	AVAILABLE_NO("Available: No");

	String name;

	SortFilterOptions(String name) {

		this.name = name;
	}

	@Override
	public String toString() {

		return name;
	}

	public static ArrayList<SortKey> getSortKey(SortFilterOptions sortFilter) {

		ArrayList<SortKey> sortKeys = new ArrayList<>();

		switch (sortFilter) {
		case NONE:
			sortKeys.add(new RowSorter.SortKey(0, SortOrder.UNSORTED));
			break;
		case NAME_ASCENDING:
			sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
			break;
		case NAME_DESCENDING:
			sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
			break;
		case PRICE_ASCENDING:
			sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
			break;
		case PRICE_DESCENDING:
			sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
			break;
		case AVAILABLE_YES:
			sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
			break;
		case AVAILABLE_NO:
			sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
			break;
		}

		return sortKeys;
	}
}