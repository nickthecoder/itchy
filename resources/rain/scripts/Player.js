Player = new Class( Behaviour )
({
    tick: function()
    {
        this.actor.moveTo( Itchy.getMouseX(), this.actor.getY() );
        
        if ( this.behaviour.touching("deadly").size() > 0 ) {
            this.actor.deathEvent("death");
        }
    }
});


