import("GridRole.js");

Soft = Class({

    Extends: GridRole,
    
    roleName: "Soft",
    
    onBirth: function() {
        new itchy.extras.Fragment().actor(this.actor).pieces( 10 ).createPoses( "fragment" );
    },
    
    onAttach: function() {
        Super();
        this.role.addTag("soft");
    },
    
    onInvaded: function( invader ) {

        new itchy.role.Explosion(this.actor)
            .projectiles(10)
            .gravity(-0.2).fade(0.9, 3.5).speed(0.1, 1.5).vy(5)
            .pose("fragment")
            .createActor();

        this.actor.kill();
    }

});

