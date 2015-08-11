package it.jrc.smart.fire.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "it.jrc.smart.fire.internal.messages.messages"; //$NON-NLS-1$

	public static String FireServiceDescription;

	public static String FireDataSource_SchemaNotSupported;

	public static String FireListView_UpdateJob_Title;

	public static String FireDataUpdate;


	//Fire Dialog
	public static String FIRE_DIALOG_TITLE;

	public static String FIRE_DATA_MODEL_NOT_CONFIGURED;

	public static String DATA_AVAILABLE_UP_TO;

	public static String NO_INFORMATION_YET_AVAILABLE;

	public static String UPDATE_BUTTON_TEXT;

	public static String BURNED_AREA;

	public static String DETERMINING_ARCHIVE_STATUS;

	public static String ACTIVE_FIRE_CHECKBOX_LABEL;
	
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

	public static String FIRE_ARCHIVE_UPDATED_FIRE_RECORDS_IMPORTED;

	public static String MODIS_BA;

	public static String DATA_MODEL_NOT_CONFIGURED_CATEGORY;

	public static String UPDATE_BURNED_AREA_ARCHIVE;

	public static String BURNED_AREA_UPDATE;

    private Messages() {
    }
}