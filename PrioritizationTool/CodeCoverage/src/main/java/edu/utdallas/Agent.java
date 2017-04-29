package edu.utdallas;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String agentArgs, Instrumentation inst) {

        System.out.println("Java Agent IS Running");

        // registers the transformer
        inst.addTransformer(new MyClassFileTransform());

    }
}

