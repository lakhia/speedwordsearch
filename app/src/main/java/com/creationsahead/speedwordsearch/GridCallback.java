package com.creationsahead.speedwordsearch;

public interface GridCallback {
    void onCellSelected(int x, int y);
    void onCellDeselected(int x, int y);
    void onGuess(int x1, int y1, int x2, int y2);
    void onWin();
}
