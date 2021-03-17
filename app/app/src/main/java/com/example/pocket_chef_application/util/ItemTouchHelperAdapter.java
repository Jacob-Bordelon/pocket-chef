package com.example.pocket_chef_application.util;

public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemSwiped(int position);
}
