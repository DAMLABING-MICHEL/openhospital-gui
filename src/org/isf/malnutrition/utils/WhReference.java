package org.isf.malnutrition.utils;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by nicosalvato on 2017-02-19.
 * Contact: nicosalvato@gmail.com
 * This class loads the reference table for W/H computation contained in the Chrt Booklet "Training Course on the
 * Management of Severe Acute Malnutrition" from Ethiopian
 */
public class WhReference {

    private static WhReference instance;
    public HashMap<BigDecimal, BigDecimal> whTable = new HashMap<BigDecimal, BigDecimal>(122, 2);

    public static synchronized WhReference getInstance() {
        if (instance == null) {
            instance = new WhReference();
        }
        return instance;
    }

    public HashMap<BigDecimal, BigDecimal> getWhTable() {
        return whTable;
    }

    private WhReference() {
        whTable.put(new BigDecimal(49).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(3.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(49.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(3.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(50).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(3.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(50.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(3.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(51).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(3.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(51.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(3.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(52).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(3.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(52.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(3.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(53).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(3.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(53.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(54).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(4.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(54.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(4.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(55).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(4.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(55.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(4.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(56).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(4.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(56.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(4.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(57).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(4.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(57.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(4.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(58).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(5.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(58.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(5.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(59).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(5.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(59.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(5.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(60).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(5.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(60.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(5.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(61).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(5.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(61.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(62).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(6.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(62.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(6.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(63).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(6.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(63.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(6.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(64).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(6.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(64.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(6.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(65).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(65.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(7.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(66).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(7.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(66.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(7.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(67).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(7.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(67.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(7.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(68).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(7.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(68.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(69).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(8.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(69.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(8.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(70).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(8.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(70.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(8.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(71).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(8.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(71.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(8.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(72).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(72.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(9.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(73).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(9.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(73.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(9.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(74).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(9.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(74.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(9.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(75).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(9.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(75.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(9.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(76).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(9.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(76.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(77).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(10.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(77.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(10.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(78).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(10.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(78.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(10.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(79).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(10.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(79.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(10.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(80).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(10.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(80.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(10.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(81).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(11).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(81.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(11.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(82).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(11.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(82.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(11.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(83).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(11.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(83.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(11.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(84).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(11.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(84.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(11.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(85).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(12).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(85.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(12.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(86).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(12.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(86.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(12.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(87).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(12.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(87.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(12.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(88).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(12.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(88.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(12.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(89).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(12.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(89.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(13).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(90).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(13.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(90.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(13.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(91).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(13.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(91.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(13.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(92).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(13.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(92.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(13.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(93).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(13.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(93.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(13.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(94).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(14).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(94.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(14.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(95).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(14.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(95.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(14.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(96).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(14.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(96.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(14.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(97).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(14.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(97.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(14.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(98).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(15).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(98.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(15.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(99).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(15.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(99.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(15.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(15.6).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(100.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(15.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(101).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(15.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(101.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(16).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(102).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(16.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(102.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(16.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(103).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(16.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(103.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(16.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(104).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(16.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(104.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(16.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(105).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(16.9).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(105.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(17.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(106).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(17.2).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(106.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(17.4).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(107).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(17.5).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(107.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(17.7).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(108).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(17.8).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(108.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(18).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(109).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(18.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(109.5).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(18.3).setScale(2, BigDecimal.ROUND_HALF_UP));
        whTable.put(new BigDecimal(110).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(18.4).setScale(2, BigDecimal.ROUND_HALF_UP));
    }
}
