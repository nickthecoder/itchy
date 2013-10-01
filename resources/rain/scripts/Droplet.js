Droplet = new Class( Behaviour )
({
    onAttach: function()
    {
        this.actor.addTag("deadly");
    },
    
    tick: function()
    {
        this.actor.moveBy( 0, - sceneBehaviour.speed );
        if ( this.actor.getY() < -10) {
            var x = random.nextInt(Itchy.getGame().getWidth());
            var y = Itchy.getGame().getHeight() + random.nextInt(10) - 5;
            this.actor.moveTo( x, y );
        }
    }
});

