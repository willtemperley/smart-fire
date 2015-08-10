package it.jrc.smart.fire.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "it.jrc.smart.fire.job.internal.messages"; //$NON-NLS-1$

	public static String FireServiceDescription;

	public static String FireDataSource_SchemaNotSupported;

	public static String FireListView_UpdateJob_Title;

	public static String FireDataUpdate;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
