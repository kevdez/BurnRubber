package com.raildeliveryservices.burnrubber.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.io.Serializable;
import java.util.HashMap;

public final class Order implements Serializable {
    public static final String TABLE_NAME = "orders";
    public static final String DATABASE_CREATE_SQL =
            "create table " + TABLE_NAME +
                    "(" +
                    Columns._ID + " integer primary key autoincrement, " +
                    Columns.FILE_NO + " integer, " +
                    Columns.DRIVER_NO + " integer, " +
                    Columns.PARENT_FILE_NO + " integer, " +
                    Columns.VOYAGE_NO + " text, " +
                    Columns.TRIP_NO + " text, " +
                    Columns.PO_NO + " text, " +
                    Columns.PICKUP_NO + " text, " +
                    Columns.RAIL_NO + " text, " +
                    Columns.MANIFEST_NO + " text, " +
                    Columns.BOOKING_NO + " text, " +
                    Columns.HAZMAT_FLAG + " integer, " +
                    Columns.APPT_DATE_TIME + " text, " +
                    Columns.APPT_TIME + " text, " +
                    Columns.MOVE_TYPE + " text, " +
                    Columns.CONTAINER_NO + " text, " +
                    Columns.CHASSIS_NO + " text, " +
                    Columns.LUMPER_FLAG + " integer, " +
                    Columns.SCALE_FLAG + " integer, " +
                    Columns.WEIGHT_FLAG + " integer, " +
                    Columns.COMMENTS + " text, " +
                    Columns.CONFIRMED_FLAG + " integer, " +
                    Columns.STARTED_FLAG + " integer, " +
                    Columns.COMPLETED_FLAG + " integer " +
                    ");";
    public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY + "/orders");
    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

    static {
        PROJECTION_MAP.put(Columns._ID, TABLE_NAME + "." + Columns._ID);
        PROJECTION_MAP.put(Columns.FILE_NO, TABLE_NAME + "." + Columns.FILE_NO);
        PROJECTION_MAP.put(Columns.DRIVER_NO, TABLE_NAME + "." + Columns.DRIVER_NO);
        PROJECTION_MAP.put(Columns.PARENT_FILE_NO, TABLE_NAME + "." + Columns.PARENT_FILE_NO);
        PROJECTION_MAP.put(Columns.VOYAGE_NO, TABLE_NAME + "." + Columns.VOYAGE_NO);
        PROJECTION_MAP.put(Columns.TRIP_NO, TABLE_NAME + "." + Columns.TRIP_NO);
        PROJECTION_MAP.put(Columns.PO_NO, TABLE_NAME + "." + Columns.PO_NO);
        PROJECTION_MAP.put(Columns.PICKUP_NO, TABLE_NAME + "." + Columns.PICKUP_NO);
        PROJECTION_MAP.put(Columns.RAIL_NO, TABLE_NAME + "." + Columns.RAIL_NO);
        PROJECTION_MAP.put(Columns.MANIFEST_NO, TABLE_NAME + "." + Columns.MANIFEST_NO);
        PROJECTION_MAP.put(Columns.BOOKING_NO, TABLE_NAME + "." + Columns.BOOKING_NO);
        PROJECTION_MAP.put(Columns.HAZMAT_FLAG, TABLE_NAME + "." + Columns.HAZMAT_FLAG);
        PROJECTION_MAP.put(Columns.APPT_DATE_TIME, TABLE_NAME + "." + Columns.APPT_DATE_TIME);
        PROJECTION_MAP.put(Columns.APPT_TIME, TABLE_NAME + "." + Columns.APPT_TIME);
        PROJECTION_MAP.put(Columns.MOVE_TYPE, TABLE_NAME + "." + Columns.MOVE_TYPE);
        PROJECTION_MAP.put(Columns.CONTAINER_NO, TABLE_NAME + "." + Columns.CONTAINER_NO);
        PROJECTION_MAP.put(Columns.CHASSIS_NO, TABLE_NAME + "." + Columns.CHASSIS_NO);
        PROJECTION_MAP.put(Columns.LUMPER_FLAG, TABLE_NAME + "." + Columns.LUMPER_FLAG);
        PROJECTION_MAP.put(Columns.SCALE_FLAG, TABLE_NAME + "." + Columns.SCALE_FLAG);
        PROJECTION_MAP.put(Columns.WEIGHT_FLAG, TABLE_NAME + "." + Columns.WEIGHT_FLAG);
        PROJECTION_MAP.put(Columns.COMMENTS, TABLE_NAME + "." + Columns.COMMENTS);

        PROJECTION_MAP.put(Columns.CONFIRMED_FLAG, TABLE_NAME + "." + Columns.CONFIRMED_FLAG);
        PROJECTION_MAP.put(Columns.STARTED_FLAG, TABLE_NAME + "." + Columns.STARTED_FLAG);
        PROJECTION_MAP.put(Columns.COMPLETED_FLAG, TABLE_NAME + "." + Columns.COMPLETED_FLAG);
    }

    private int id;
    private int fileNo;
    private int driverNo;
    private int parentFileNo;
    private String voyageNo;
    private String tripNo;
    private String poNo;
    private String pickUpNo;
    private String railNo;
    private String manifestNo;
    private String bookingNo;
    private String appointmentDate;
    private String appointmentTime;
    private String moveType;
    private String containerNo;
    private String chassisNo;
    private String comment;
    private int hazmatFlag;
    private int lumperFlag;
    private int scaleFlag;
    private int weightFlag;
    private int confirmFlag;
    private int startFlag;
    private int completedFlag;

    public String getId() {
        return String.valueOf(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileNo() {
        return String.valueOf(fileNo);
    }

    public void setFileNo(int fileNo) {
        this.fileNo = fileNo;
    }

    public String getDriverNo() {
        return String.valueOf(driverNo);
    }

    public void setDriverNo(int driverNo) {
        this.driverNo = driverNo;
    }

    public String getParentFileNo() {
        return String.valueOf(parentFileNo);
    }

    public void setParentFileNo(int parentFileNo) {
        this.parentFileNo = parentFileNo;
    }

    public String getVoyageNo() {
        return voyageNo;
    }

    public void setVoyageNo(String voyageNo) {
        this.voyageNo = voyageNo;
    }

    public String getTripNo() {
        return tripNo;
    }

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
    }

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public String getPickUpNo() {
        return pickUpNo;
    }

    public void setPickUpNo(String pickUpNo) {
        this.pickUpNo = pickUpNo;
    }

    public String getRailNo() {
        return railNo;
    }

    public void setRailNo(String railNo) {
        this.railNo = railNo;
    }

    public String getManifestNo() {
        return manifestNo;
    }

    public void setManifestNo(String manifestNo) {
        this.manifestNo = manifestNo;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getMoveType() {
        return moveType;
    }

    public void setMoveType(String moveType) {
        this.moveType = moveType;
    }

    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    public String getChassisNo() {
        return chassisNo;
    }

    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getHazmatFlag() {
        return hazmatFlag;
    }

    public void setHazmatFlag(int hazmatFlag) {
        this.hazmatFlag = hazmatFlag;
    }

    public int getLumperFlag() {
        return lumperFlag;
    }

    public void setLumperFlag(int lumperFlag) {
        this.lumperFlag = lumperFlag;
    }

    public int getScaleFlag() {
        return scaleFlag;
    }

    public void setScaleFlag(int scaleFlag) {
        this.scaleFlag = scaleFlag;
    }

    public int getWeightFlag() {
        return weightFlag;
    }

    public void setWeightFlag(int weightFlag) {
        this.weightFlag = weightFlag;
    }

    public int getConfirmFlag() {
        return confirmFlag;
    }

    public void setConfirmFlag(int confirmFlag) {
        this.confirmFlag = confirmFlag;
    }

    public int getStartFlag() {
        return startFlag;
    }

    public void setStartFlag(int startFlag) {
        this.startFlag = startFlag;
    }

    public int getCompletedFlag() {
        return completedFlag;
    }

    public void setCompletedFlag(int completedFlag) {
        this.completedFlag = completedFlag;
    }

    public static final class Columns implements BaseColumns {
        public static final String FILE_NO = "file_no";
        public static final String DRIVER_NO = "driver_no";
        public static final String PARENT_FILE_NO = "parent_file_no";
        public static final String VOYAGE_NO = "voyage_no";
        public static final String TRIP_NO = "trip_no";
        public static final String PO_NO = "po_no";
        public static final String PICKUP_NO = "pickup_no";
        public static final String RAIL_NO = "rail_no";
        public static final String MANIFEST_NO = "manifest_no";
        public static final String BOOKING_NO = "booking_no";
        public static final String HAZMAT_FLAG = "hazmat_flag";
        public static final String APPT_DATE_TIME = "appt_date_time";
        public static final String APPT_TIME = "appt_time";
        public static final String MOVE_TYPE = "move_type";
        public static final String CONFIRMED_FLAG = "confirmed_flag";
        public static final String CONTAINER_NO = "container_no";
        public static final String CHASSIS_NO = "chassis_no";
        public static final String LUMPER_FLAG = "lumper_flag";
        public static final String SCALE_FLAG = "scale_flag";
        public static final String WEIGHT_FLAG = "weight_flag";
        public static final String COMMENTS = "comments";
        public static final String STARTED_FLAG = "started_flag";
        public static final String COMPLETED_FLAG = "completed_flag";
    }


}
