package com.hansjin.mukja_android.NearbyRestaurant.search;

import java.util.List;

public interface OnFinishSearchListener {
	public void onSuccess(List<Item> itemList);
	public void onFail();
}
