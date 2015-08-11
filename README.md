# SMART-fire-plugin

Within tropical ecosystems, fire is a widespread natural phenomenon as well as a result of human activities, such as land clearance for agriculture. Poachers notoriously use fire to clear vegetation to control movement of wild animals and increase their visibility. Information on fire occurrence is therefore essential to track and identify human induced threats and pressures and quickly react to them.
 
A standardized law enforcement and ecological monitoring and reporting tool, called SMART, has been developed by a partnership of conservation institutions for strategic management of conservation areas. In this context we are developing a Fire Monitoring plugin for SMART which will provide access to satellite data of fire occurrence.
 
Fire occurrence can be tracked in near-real time using publicly available NASA-FIRMS fire products derived from the MODIS sensor. Although these data are freely available, significant technical barriers prevent their usage where they are needed most, such as in protected areas. Our aim is to ensure conservation practitioners can access this information in the simplest and most timely manner possible.
 
A plugin has been developed which retrieves MODIS fire information and integrates this into the SMART data model, thereby providing query and reporting tools built into SMART direct access to fire occurrence and burned area data.  The near real-time nature of the fire information, combined with field derived intelligence provides opportunities for active management.

Historical fire information (from 2002 onwards) can also be retrieved, which can be used to define the status of the habitat and identify trends and anomalies.

# Installation

The data model must be manually configured to include two categories, one to hold active fire observations and one to hold burned area observations.  The active fire category should have two attributes named "confidence" (the probability of the fire observation being correct) and "frp" (the fire radiative power).  This is represented here in tree form, with the keys in brackets.

File -> Install new plugins
Select "Add" then "local".  Select the unzipped zip file and install.

+ ActiveFire (activefire)
|- frp (frp)
|- confidence (confidence)
+ BurnedArea (burnedarea)

