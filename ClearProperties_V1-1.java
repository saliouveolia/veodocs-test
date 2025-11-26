import com.altirnao.aodocs.custom.*;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;

/*
Created by Nicolas Tourneur, Saliou Djalo
Copyright © 2025 VWT Gulf Platform. All rights reserved.

Clear Properties V 1.1
Workflow Transition Action
Run with admin privileges, no async execution
*/

class MyFunctions {
    public Boolean isFieldValid(String className, String fieldName) {
        ImmutableList< FieldInfo > fields = getDocumentService().getClassFields(className);
        for (FieldInfo field : fields) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
}

try {

    MyFunctions func = new MyFunctions();
    Document doc = getDocumentService().lockDocument(document);

    // Get the parameter containing the comma-separated list of properties
    String propertiesToClear = getParam("Properties List");

    // Check if the parameter is not empty
    if (propertiesToClear == null || propertiesToClear.isEmpty()) {
        debug("The parameter of the script is empty. Please fill it in!");
        return;
    }

    // Convert the comma-separated string into a list
    List<String> propertiesList = Arrays.asList(propertiesToClear.split(","));

    // Loop through each property in the list
    for (String property : propertiesList) {
        property = property.trim(); // Trim whitespace for safety

        // Check if the field is valid
        if (!func.isFieldValid(doc.getDocumentClass(), property)) {
            debug("Property: " + property + " doesn't exist!");
            continue; // Skip invalid properties
        }

        // Update Sep 15, 2025 : V 1.1
        // Check if the property is a calculated field
        FieldInfo fieldInfo = doc.getFields().getFieldByName(property);
        FieldDefinition fieldDefinition = fieldInfo.getFieldDefinition();

        // Check if the property is a calculated field
        boolean hasCalculatedValue = fieldDefinition.hasCalculatedValue();
        if(hasCalculatedValue){continue;}

        // Clear the property (set to null)
        doc.setField(property, null);
        // debug("Cleared property: " + property);
    }

} catch (Exception e) {
    debug("Error executing the script: error=" + e.getMessage() + " user=" + getPermissionService().getCurrentUser());
    throw e;
}
