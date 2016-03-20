package com.sam_chordas.android.stockhawk.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import java.util.Date;

/**
 * Created by jon on 3/10/2016.
 */
public class HistoricalQuoteColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
    public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String SYMBOL = "symbol";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String CLOSE_DATE = "close_date";
    @DataType(DataType.Type.REAL ) @NotNull
    public static final String OPEN = "open";
    @DataType(DataType.Type.REAL ) @NotNull
    public static final String HIGH = "high";
    @DataType(DataType.Type.REAL ) @NotNull
    public static final String LOW = "low";
    @DataType(DataType.Type.REAL ) @NotNull
    public static final String CLOSE = "close";
    @DataType(DataType.Type.REAL ) @NotNull
    public static final String ADJ_CLOSE = "adj_close";

}
