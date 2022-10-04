package dev.autoauto.model;

public interface AutoautoProgram extends AutoautoProgramElement {
    void init();
    void loop();
    void stepInit();
}
