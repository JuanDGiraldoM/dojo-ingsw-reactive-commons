package com.bancolombia.dojo.reactivecommons.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveWho {
    public static final String NAME = "system.save.who";
    private String who;
    private String appName;
}
