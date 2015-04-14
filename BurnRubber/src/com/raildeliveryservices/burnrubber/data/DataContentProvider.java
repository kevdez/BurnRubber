package com.raildeliveryservices.burnrubber.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class DataContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.raildeliveryservices.burnrubber";

    private static final int ORDERS = 1;
    private static final int ORDER = 2;
    private static final int LEGS = 3;
    private static final int LEG = 4;
    private static final int LEG_EXTRAS = 5;
    private static final int LEG_EXTRA = 6;
    private static final int LEG_OUTBOUNDS = 7;
    private static final int LEG_OUTBOUND = 8;
    private static final int MESSAGES = 9;
    private static final int MESSAGE = 10;
    private static final int UPLOADQS = 11;
    private static final int UPLOADQ = 12;
    private static final int FORMS = 13;
    private static final int FORM = 14;
    private static final int MESSAGE_ALERTS = 15;
    private static final int MESSAGE_ALERT = 16;

    private static final UriMatcher _uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        _uriMatcher.addURI(AUTHORITY, "orders", ORDERS);
        _uriMatcher.addURI(AUTHORITY, "orders/#", ORDER);
        _uriMatcher.addURI(AUTHORITY, "legs", LEGS);
        _uriMatcher.addURI(AUTHORITY, "legs/#", LEG);
        _uriMatcher.addURI(AUTHORITY, "legextras", LEG_EXTRAS);
        _uriMatcher.addURI(AUTHORITY, "legextras/#", LEG_EXTRA);
        _uriMatcher.addURI(AUTHORITY, "legoutbound", LEG_OUTBOUNDS);
        _uriMatcher.addURI(AUTHORITY, "legoutbound/#", LEG_OUTBOUND);
        _uriMatcher.addURI(AUTHORITY, "messages", MESSAGES);
        _uriMatcher.addURI(AUTHORITY, "messages/#", MESSAGE);
        _uriMatcher.addURI(AUTHORITY, "uploadq", UPLOADQS);
        _uriMatcher.addURI(AUTHORITY, "uploadq/#", UPLOADQ);
        _uriMatcher.addURI(AUTHORITY, "forms", FORMS);
        _uriMatcher.addURI(AUTHORITY, "forms/#", FORM);
        _uriMatcher.addURI(AUTHORITY, "messagealerts", MESSAGE_ALERTS);
        _uriMatcher.addURI(AUTHORITY, "messagealerts/#", MESSAGE_ALERT);
    }

    private DatabaseHelper _databaseHelper;

    @Override
    public boolean onCreate() {
        _databaseHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        switch (_uriMatcher.match(uri)) {
            case ORDERS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.orders";
            case ORDER:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.orders";
            case LEGS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.legs";
            case LEG:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.legs";
            case LEG_EXTRAS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.legextras";
            case LEG_EXTRA:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.legextras";
            case LEG_OUTBOUNDS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.legoutbound";
            case LEG_OUTBOUND:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.legoutbound";
            case MESSAGES:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.messages";
            case MESSAGE:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.messages";
            case UPLOADQS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.uploadq";
            case UPLOADQ:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.uploadq";
            case FORMS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.forms";
            case FORM:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.forms";
            case MESSAGE_ALERTS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.messagealerts";
            case MESSAGE_ALERT:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.raildeliveryservices.burnrubber.provider.messagealert";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = _databaseHelper.getReadableDatabase();
        String where = "";

        switch (_uriMatcher.match(uri)) {
            case ORDERS:
                qb.setTables(Order.TABLE_NAME);
                qb.setProjectionMap(Order.PROJECTION_MAP);
                break;
            case ORDER:
                qb.setTables(Order.TABLE_NAME);
                qb.setProjectionMap(Order.PROJECTION_MAP);

                where = selection;
                selection = Order.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case LEGS:
                qb.setTables(Leg.TABLE_NAME);
                qb.setProjectionMap(Leg.PROJECTION_MAP);
                break;
            case LEG:
                qb.setTables(Leg.TABLE_NAME);
                qb.setProjectionMap(Leg.PROJECTION_MAP);

                where = selection;
                selection = Leg.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case LEG_EXTRAS:
                qb.setTables(LegExtra.TABLE_NAME);
                qb.setProjectionMap(LegExtra.PROJECTION_MAP);
                break;
            case LEG_EXTRA:
                qb.setTables(LegExtra.TABLE_NAME);
                qb.setProjectionMap(LegExtra.PROJECTION_MAP);

                where = selection;
                selection = LegExtra.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case LEG_OUTBOUNDS:
                qb.setTables(LegOutbound.TABLE_NAME);
                qb.setProjectionMap(LegOutbound.PROJECTION_MAP);
                break;
            case LEG_OUTBOUND:
                qb.setTables(LegOutbound.TABLE_NAME);
                qb.setProjectionMap(LegOutbound.PROJECTION_MAP);

                where = selection;
                selection = LegOutbound.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case MESSAGES:
                qb.setTables(Message.TABLE_NAME);
                qb.setProjectionMap(Message.PROJECTION_MAP);
                break;
            case MESSAGE:
                qb.setTables(Message.TABLE_NAME);
                qb.setProjectionMap(Message.PROJECTION_MAP);

                where = selection;
                selection = Message.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case UPLOADQS:
                qb.setTables(UploadQ.TABLE_NAME);
                qb.setProjectionMap(UploadQ.PROJECTION_MAP);
                break;
            case UPLOADQ:
                qb.setTables(UploadQ.TABLE_NAME);
                qb.setProjectionMap(UploadQ.PROJECTION_MAP);

                where = selection;
                selection = UploadQ.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case FORMS:
                qb.setTables(Form.TABLE_NAME);
                qb.setProjectionMap(Form.PROJECTION_MAP);
                break;
            case FORM:
                qb.setTables(Form.TABLE_NAME);
                qb.setProjectionMap(Form.PROJECTION_MAP);

                where = selection;
                selection = Form.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case MESSAGE_ALERTS:
                qb.setTables(MessageAlert.TABLE_NAME);
                qb.setProjectionMap(MessageAlert.PROJECTION_MAP);
                break;
            case MESSAGE_ALERT:
                qb.setTables(MessageAlert.TABLE_NAME);
                qb.setProjectionMap(MessageAlert.PROJECTION_MAP);

                where = selection;
                selection = MessageAlert.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (!TextUtils.isEmpty(where)) {
            selection += " AND " + where;
        }

        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = _databaseHelper.getWritableDatabase();
        String tableName = "";
        String nullColHack = "";
        long recId = 0;
        Uri contentUri;

        switch (_uriMatcher.match(uri)) {
            case ORDERS:
                tableName = Order.TABLE_NAME;
                nullColHack = Order.Columns.FILE_NO;
                contentUri = Order.CONTENT_URI;
                break;
            case LEGS:
                tableName = Leg.TABLE_NAME;
                nullColHack = Leg.Columns.ORDER_ID;
                contentUri = Leg.CONTENT_URI;
                break;
            case LEG_EXTRAS:
                tableName = LegExtra.TABLE_NAME;
                nullColHack = LegExtra.Columns.LEG_ID;
                contentUri = LegExtra.CONTENT_URI;
                break;
            case LEG_OUTBOUNDS:
                tableName = LegOutbound.TABLE_NAME;
                nullColHack = LegOutbound.Columns.LEG_ID;
                contentUri = LegOutbound.CONTENT_URI;
                break;
            case MESSAGES:
                tableName = Message.TABLE_NAME;
                nullColHack = Message.Columns.MESSAGE_TEXT;
                contentUri = Message.CONTENT_URI;
                break;
            case UPLOADQS:
                tableName = UploadQ.TABLE_NAME;
                nullColHack = UploadQ.Columns.SERVICE_URL;
                contentUri = UploadQ.CONTENT_URI;
                break;
            case FORMS:
                tableName = Form.TABLE_NAME;
                nullColHack = Form.Columns.FORM_NAME;
                contentUri = Form.CONTENT_URI;
                break;
            case MESSAGE_ALERTS:
                tableName = MessageAlert.TABLE_NAME;
                nullColHack = MessageAlert.Columns.MESSAGE_FLAG;
                contentUri = MessageAlert.CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        recId = db.insert(
                tableName,
                nullColHack,
                values);

        if (recId > 0) {
            Uri newUri = ContentUris.withAppendedId(contentUri, recId);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }

        throw new SQLiteException("Could not insert row into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = _databaseHelper.getWritableDatabase();
        String tableName = "";
        String where = "";
        int updateCount = 0;

        switch (_uriMatcher.match(uri)) {
            case ORDERS:
                tableName = Order.TABLE_NAME;
                break;
            case ORDER:
                tableName = Order.TABLE_NAME;
                where = selection;
                selection = Order.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case LEGS:
                tableName = Leg.TABLE_NAME;
                break;
            case LEG:
                tableName = Leg.TABLE_NAME;
                where = selection;
                selection = Leg.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case LEG_EXTRAS:
                tableName = LegExtra.TABLE_NAME;
                break;
            case LEG_EXTRA:
                tableName = LegExtra.TABLE_NAME;
                where = selection;
                selection = LegExtra.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case LEG_OUTBOUNDS:
                tableName = LegOutbound.TABLE_NAME;
                break;
            case LEG_OUTBOUND:
                tableName = LegOutbound.TABLE_NAME;
                where = selection;
                selection = LegOutbound.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case MESSAGES:
                tableName = Message.TABLE_NAME;
                break;
            case MESSAGE:
                tableName = Message.TABLE_NAME;
                where = selection;
                selection = Message.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case UPLOADQS:
                tableName = UploadQ.TABLE_NAME;
                break;
            case UPLOADQ:
                tableName = UploadQ.TABLE_NAME;
                where = selection;
                selection = UploadQ.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case FORMS:
                tableName = Form.TABLE_NAME;
                break;
            case FORM:
                tableName = Form.TABLE_NAME;
                where = selection;
                selection = Form.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case MESSAGE_ALERTS:
                tableName = MessageAlert.TABLE_NAME;
                break;
            case MESSAGE_ALERT:
                tableName = MessageAlert.TABLE_NAME;
                where = selection;
                selection = MessageAlert.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (!TextUtils.isEmpty(where)) {
            selection += " AND " + where;
        }

        updateCount = db.update(
                tableName,
                values,
                selection,
                selectionArgs);

        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = _databaseHelper.getWritableDatabase();
        String tableName = "";
        String where = "";
        int deleteCount = 0;

        switch (_uriMatcher.match(uri)) {
            case ORDERS:
                tableName = Order.TABLE_NAME;
                break;
            case ORDER:
                tableName = Order.TABLE_NAME;
                where = selection;
                selection = Order.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case LEGS:
                tableName = Leg.TABLE_NAME;
                break;
            case LEG:
                tableName = Leg.TABLE_NAME;
                where = selection;
                selection = Leg.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case LEG_EXTRAS:
                tableName = LegExtra.TABLE_NAME;
                break;
            case LEG_EXTRA:
                tableName = LegExtra.TABLE_NAME;
                where = selection;
                selection = LegExtra.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case LEG_OUTBOUNDS:
                tableName = LegOutbound.TABLE_NAME;
                break;
            case LEG_OUTBOUND:
                tableName = LegOutbound.TABLE_NAME;
                where = selection;
                selection = LegOutbound.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case MESSAGES:
                tableName = Message.TABLE_NAME;
                break;
            case MESSAGE:
                tableName = Message.TABLE_NAME;
                where = selection;
                selection = Message.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case UPLOADQS:
                tableName = UploadQ.TABLE_NAME;
                break;
            case UPLOADQ:
                tableName = UploadQ.TABLE_NAME;
                where = selection;
                selection = UploadQ.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case FORMS:
                tableName = Form.TABLE_NAME;
                break;
            case FORM:
                tableName = Form.TABLE_NAME;
                where = selection;
                selection = Form.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            case MESSAGE_ALERTS:
                tableName = MessageAlert.TABLE_NAME;
                break;
            case MESSAGE_ALERT:
                tableName = MessageAlert.TABLE_NAME;
                where = selection;
                selection = MessageAlert.Columns._ID + " = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (!TextUtils.isEmpty(where)) {
            selection += " AND " + where;
        }

        deleteCount = db.delete(
                tableName,
                selection,
                selectionArgs);

        if (deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleteCount;
    }
}
