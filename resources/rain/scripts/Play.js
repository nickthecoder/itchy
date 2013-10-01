Play = new Class( SceneBehaviour )
({
    __init__: function() {
        this.speed = 5;
        this.levelUp = itchy.extras.Timer.createTimerSeconds( 10 );
    },

    tick: function() {
        if (this.levelUp.isFinished()) {
            this.speed += 1;
            stdout.println("Increase Speed to " + this.speed );
            this.levelUp.reset();
        }
    }  
});

