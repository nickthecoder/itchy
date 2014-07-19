import("GridRole.js");

Wall = Class({

    Extends: GridRole,
    
    roleName: "Wall",
    
    onAttach: function() {
        var props = this.getCostumeProperties();
        
        if (props.roundedNE) {
            this.role.addTag("roundedNE");
        }
        if (props.roundedSE) {
            this.role.addTag("roundedSE");
        }
        if (props.roundedSW) {
            this.role.addTag("roundedSW");
        }
        if (props.roundedNW) {
            this.role.addTag("roundedNW");
        }
    },

});

Wall.properties = new java.util.ArrayList();

