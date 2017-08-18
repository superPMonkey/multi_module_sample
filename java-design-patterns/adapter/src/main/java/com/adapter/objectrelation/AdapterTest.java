package com.adapter.objectrelation;

import com.iluwatar.adapter.Engineer;
import com.iluwatar.adapter.GnomeEngineer;

/**
 * Created by IntelliJ IDEA.
 * Author: Eric
 * Date:   2017/8/18 15:05
 * Desc:
 */
public class AdapterTest {

    public static void main(String[] args) {
        Engineer engineer = new GnomeEngineer();
        EngineerManager manager = new EngineerManager(engineer);
        manager.operateDevice();
    }

}
