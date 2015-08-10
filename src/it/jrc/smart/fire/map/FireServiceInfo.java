package it.jrc.smart.fire.map;

import it.jrc.smart.fire.internal.messages.Messages;
import net.refractions.udig.catalog.IServiceInfo;


/**
 * Fire service info
 * 
 * @author willtemperley@gmail.com
 *
 */
public class FireServiceInfo extends IServiceInfo{

	public FireServiceInfo(FireService service){
		this.description = Messages.FireServiceDescription;
		this.title = service.getTitle();
	}
	
}
