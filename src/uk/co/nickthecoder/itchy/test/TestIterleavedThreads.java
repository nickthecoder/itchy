package uk.co.nickthecoder.itchy.test;

import uk.co.nickthecoder.itchy.util.InterleavedThread;
import uk.co.nickthecoder.itchy.util.InterleavedThreads;

public class TestIterleavedThreads
{
    public StringBuffer result = new StringBuffer();

    public static void main( String[] args )
    {
        TestIterleavedThreads test = new TestIterleavedThreads();
        test.run();

    }

    public void run()
    {
        InterleavedThreads its = new InterleavedThreads();
        for (int i = 0; i < 10; i++) {
            TestThread x = new TestThread(i);
            its.add(x);
        }
        its.start();
    }

    public class TestThread extends InterleavedThread
    {
        private int id;

        public TestThread( int id )
        {
            this.id = id;

        }

        @Override
        public void work()
        {
            TestIterleavedThreads.this.result.append("Starting thread ").append(this.id);
            for (int j = 0; j < 10; j++) {
                TestIterleavedThreads.this.result.append(this.id).append("(").append(j).append(")");
                this.next();
            }
            TestIterleavedThreads.this.result.append("Finished thread ").append(this.id)
                    .append("\n");
        }

    }
}
