import java.util.*;

public class SLRParseTable {
  public static Map<String, Map<String, String>> initializeParseTable() {
    Map<String, Map<String, String>> parseTable = new HashMap<>();

    // State 0
    Map<String, String> state0 = new HashMap<>();
    state0.put("main", "s2");
    state0.put("PROG", "1");
    parseTable.put("0", state0);

    // State 1
    Map<String, String> state1 = new HashMap<>();
    state1.put("$", "acc");
    parseTable.put("1", state1);

    // State 2
    Map<String, String> state2 = new HashMap<>();
    state2.put("num", "s5");
    state2.put("text", "s6");
    state2.put("begin", "r2");
    state2.put("GLOBVARS", "3");
    state2.put("VTYP", "4");
    parseTable.put("2", state2);

    // State 3
    Map<String, String> state3 = new HashMap<>();
    state3.put("begin", "s8");
    state3.put("ALGO", "7");
    parseTable.put("3", state3);

    // State 4
    Map<String, String> state4 = new HashMap<>();
    state4.put("V_", "s10");
    state4.put("VNAME", "9");
    parseTable.put("4", state4);

    // State 5
    Map<String, String> state5 = new HashMap<>();
    state5.put("V_", "r4");
    parseTable.put("5", state5);

    // State 6
    Map<String, String> state6 = new HashMap<>();
    state6.put("V_", "r5");
    parseTable.put("6", state6);

    // State 7
    Map<String, String> state7 = new HashMap<>();
    state7.put("num", "s15");
    state7.put("end", "r48");
    state7.put("void", "s16");
    state7.put("$", "r48");
    state7.put("FUNCTIONS", "11");
    state7.put("DECL", "12");
    state7.put("HEADER", "13");
    state7.put("FTYP", "14");
    parseTable.put("7", state7);

    // State 8
    Map<String, String> state8 = new HashMap<>();
    state8.put("V_", "s10");
    state8.put("end", "r8");
    state8.put("skip", "s19");
    state8.put("halt", "s20");
    state8.put("print", "s21");
    state8.put("return", "s22");
    state8.put("if", "s28");
    state8.put("F_", "s29");
    state8.put("VNAME", "26");
    state8.put("INSTRUC", "17");
    state8.put("COMMAND", "18");
    state8.put("ASSIGN", "23");
    state8.put("CALL", "24");
    state8.put("BRANCH", "25");
    state8.put("FNAME", "27");
    parseTable.put("8", state8);

    // State 9
    Map<String, String> state9 = new HashMap<>();
    state9.put(",", "s30");
    parseTable.put("9", state9);

    // State 10
    Map<String, String> state10 = new HashMap<>();
    state10.put(",", "r6");
    state10.put(";", "r6");
    state10.put("input", "r6");
    state10.put("=", "r6");
    state10.put(")", "r6");
    parseTable.put("10", state10);

    // State 11
    Map<String, String> state11 = new HashMap<>();
    state11.put("$", "r1");
    parseTable.put("11", state11);

    // State 12
    Map<String, String> state12 = new HashMap<>();
    state12.put("num", "s15");
    state12.put("end", "r48");
    state12.put("void", "s16");
    state12.put("$", "r48");
    state12.put("FUNCTIONS", "31");
    state12.put("DECL", "12");
    state12.put("HEADER", "13");
    state12.put("FTYP", "14");
    parseTable.put("12", state12);

    // State 13
    Map<String, String> state13 = new HashMap<>();
    state13.put("{", "s34");
    state13.put("BODY", "32");
    state13.put("PROLOG", "33");
    parseTable.put("13", state13);

    // State 14
    Map<String, String> state14 = new HashMap<>();
    state14.put("F_", "s29");
    state14.put("FNAME", "35");
    parseTable.put("14", state14);

    // State 15
    Map<String, String> state15 = new HashMap<>();
    state15.put("F_", "r52");
    parseTable.put("15", state15);

    // State 16
    Map<String, String> state16 = new HashMap<>();
    state16.put("F_", "r53");
    parseTable.put("16", state16);

    // State 17
    Map<String, String> state17 = new HashMap<>();
    state17.put("end", "s36");
    parseTable.put("17", state17);

    // State 18
    Map<String, String> state18 = new HashMap<>();
    state18.put(";", "s37");
    parseTable.put("18", state18);

    // State 19
    Map<String, String> state19 = new HashMap<>();
    state19.put(";", "r10");
    parseTable.put("19", state19);

    // State 20
    Map<String, String> state20 = new HashMap<>();
    state20.put(";", "r11");
    parseTable.put("20", state20);

    // State 21
    Map<String, String> state21 = new HashMap<>();
    state21.put("V_", "s10");
    state21.put("N_", "s41");
    state21.put("T_", "s42");
    state21.put("VNAME", "39");
    state21.put("ATOMIC", "38");
    state21.put("CONST", "40");
    parseTable.put("21", state21);

    // State 22
    Map<String, String> state22 = new HashMap<>();
    state22.put("V_", "s10");
    state22.put("N_", "s41");
    state22.put("T_", "s42");
    state22.put("VNAME", "39");
    state22.put("ATOMIC", "43");
    state22.put("CONST", "40");
    parseTable.put("22", state22);

    // State 23
    Map<String, String> state23 = new HashMap<>();
    state23.put(";", "r14");
    parseTable.put("23", state23);

    // State 24
    Map<String, String> state24 = new HashMap<>();
    state24.put(";", "r15");
    parseTable.put("24", state24);

    // State 25
    Map<String, String> state25 = new HashMap<>();
    state25.put(";", "r16");
    parseTable.put("25", state25);

    // State 26
    Map<String, String> state26 = new HashMap<>();
    state26.put("input", "s44");
    state26.put("=", "s45");
    parseTable.put("26", state26);

    // State 27
    Map<String, String> state27 = new HashMap<>();
    state27.put("(", "s46");
    parseTable.put("27", state27);

    // State 28
    Map<String, String> state28 = new HashMap<>();
    state28.put("not", "s60");
    state28.put("sqrt", "s61");
    state28.put("or", "s52");
    state28.put("and", "s53");
    state28.put("eq", "s54");
    state28.put("grt", "s55");
    state28.put("add", "s56");
    state28.put("sub", "s57");
    state28.put("mul", "s58");
    state28.put("div", "s59");
    state28.put("COND", "47");
    state28.put("SIMPLE", "48");
    state28.put("COMPOSIT", "49");
    state28.put("UNOP", "51");
    state28.put("BINOP", "50");
    parseTable.put("28", state28);

    // State 29
    Map<String, String> state29 = new HashMap<>();
    state29.put("(", "r47");
    parseTable.put("29", state29);

    // State 30
    Map<String, String> state30 = new HashMap<>();
    state30.put("num", "s5");
    state30.put("text", "s6");
    state30.put("begin", "r2");
    state30.put("GLOBVARS", "62");
    state30.put("VTYP", "4");
    parseTable.put("30", state30);

    // State 31
    Map<String, String> state31 = new HashMap<>();
    state31.put("end", "r49");
    state31.put("$", "r49");
    parseTable.put("31", state31);

    // State 32
    Map<String, String> state32 = new HashMap<>();
    state32.put("num", "r50");
    state32.put("end", "r50");
    state32.put("void", "r50");
    state32.put("$", "r50");
    parseTable.put("32", state32);

    // State 33
    Map<String, String> state33 = new HashMap<>();
    state33.put("num", "s5");
    state33.put("text", "s6");
    state33.put("VTYP", "64");
    state33.put("LOCVARS", "63");
    parseTable.put("33", state33);

    // State 34
    Map<String, String> state34 = new HashMap<>();
    state34.put("num", "r55");
    state34.put("text", "r55");
    parseTable.put("34", state34);

    // State 35
    Map<String, String> state35 = new HashMap<>();
    state35.put("(", "s65");
    parseTable.put("35", state35);

    // State 36
    Map<String, String> state36 = new HashMap<>();
    state36.put("num", "r7");
    state36.put(";", "r7");
    state36.put("else", "r7");
    state36.put("void", "r7");
    state36.put("}", "r7");
    state36.put("$", "r7");
    parseTable.put("36", state36);

    // State 37
    Map<String, String> state37 = new HashMap<>();
    state37.put("V_", "s10");
    state37.put("end", "r8");
    state37.put("skip", "s19");
    state37.put("halt", "s20");
    state37.put("print", "s21");
    state37.put("return", "s22");
    state37.put("if", "s28");
    state37.put("F_", "s29");
    state37.put("VNAME", "26");
    state37.put("INSTRUC", "66");
    state37.put("COMMAND", "18");
    state37.put("ASSIGN", "23");
    state37.put("CALL", "24");
    state37.put("BRANCH", "25");
    state37.put("FNAME", "27");
    parseTable.put("37", state37);

    // State 38
    Map<String, String> state38 = new HashMap<>();
    state38.put(";", "r12");
    parseTable.put("38", state38);

    // State 39
    Map<String, String> state39 = new HashMap<>();
    state39.put(",", "r17");
    state39.put(";", "r17");
    state39.put(")", "r17");
    parseTable.put("39", state39);

    // State 40
    Map<String, String> state40 = new HashMap<>();
    state40.put(",", "r18");
    state40.put(";", "r18");
    state40.put(")", "r18");
    parseTable.put("40", state40);

    // State 41
    Map<String, String> state41 = new HashMap<>();
    state41.put(",", "r19");
    state41.put(";", "r19");
    state41.put(")", "r19");
    parseTable.put("41", state41);

    // State 42
    Map<String, String> state42 = new HashMap<>();
    state42.put(",", "r20");
    state42.put(";", "r20");
    state42.put(")", "r20");
    parseTable.put("42", state42);

    // State 43
    Map<String, String> state43 = new HashMap<>();
    state43.put(";", "r13");
    parseTable.put("43", state43);

    // State 44
    Map<String, String> state44 = new HashMap<>();
    state44.put(";", "r21");
    parseTable.put("44", state44);

    // State 45
    Map<String, String> state45 = new HashMap<>();
    state45.put("V_", "s10");
    state45.put("N_", "s41");
    state45.put("T_", "s42");
    state45.put("not", "s60");
    state45.put("sqrt", "s61");
    state45.put("or", "s52");
    state45.put("and", "s53");
    state45.put("eq", "s54");
    state45.put("grt", "s55");
    state45.put("add", "s56");
    state45.put("sub", "s57");
    state45.put("mul", "s58");
    state45.put("div", "s59");
    state45.put("F_", "s29");
    state45.put("VNAME", "39");
    state45.put("ATOMIC", "68");
    state45.put("CONST", "40");
    state45.put("CALL", "69");
    state45.put("TERM", "67");
    state45.put("OP", "70");
    state45.put("UNOP", "71");
    state45.put("BINOP", "72");
    state45.put("FNAME", "27");
    parseTable.put("45", state45);

    // State 46
    Map<String, String> state46 = new HashMap<>();
    state46.put("V_", "s10");
    state46.put("N_", "s41");
    state46.put("T_", "s42");
    state46.put("VNAME", "39");
    state46.put("ATOMIC", "73");
    state46.put("CONST", "40");
    parseTable.put("46", state46);

    // State 47
    Map<String, String> state47 = new HashMap<>();
    state47.put("then", "s74");
    parseTable.put("47", state47);

    // State 48
    Map<String, String> state48 = new HashMap<>();
    state48.put("then", "r32");
    parseTable.put("48", state48);

    // State 49
    Map<String, String> state49 = new HashMap<>();
    state49.put("then", "r33");
    parseTable.put("49", state49);

    // State 50
    Map<String, String> state50 = new HashMap<>();
    state50.put("(", "s75");
    parseTable.put("50", state50);

    // State 51
    Map<String, String> state51 = new HashMap<>();
    state51.put("(", "s76");
    parseTable.put("51", state51);

    // State 52
    Map<String, String> state52 = new HashMap<>();
    state52.put("(", "r39");
    parseTable.put("52", state52);

    // State 53
    Map<String, String> state53 = new HashMap<>();
    state53.put("(", "r40");
    parseTable.put("53", state53);

    // State 54
    Map<String, String> state54 = new HashMap<>();
    state54.put("(", "r41");
    parseTable.put("54", state54);

    // State 55
    Map<String, String> state55 = new HashMap<>();
    state55.put("(", "r42");
    parseTable.put("55", state55);

    // State 56
    Map<String, String> state56 = new HashMap<>();
    state56.put("(", "r43");
    parseTable.put("56", state56);

    // State 57
    Map<String, String> state57 = new HashMap<>();
    state57.put("(", "r44");
    parseTable.put("57", state57);

    // State 58
    Map<String, String> state58 = new HashMap<>();
    state58.put("(", "r45");
    parseTable.put("58", state58);

    // State 59
    Map<String, String> state59 = new HashMap<>();
    state59.put("(", "r46");
    parseTable.put("59", state59);

    // State 60
    Map<String, String> state60 = new HashMap<>();
    state60.put("(", "r37");
    parseTable.put("60", state60);

    // State 61
    Map<String, String> state61 = new HashMap<>();
    state61.put("(", "r38");
    parseTable.put("61", state61);

    // State 62
    Map<String, String> state62 = new HashMap<>();
    state62.put("begin", "r3");
    parseTable.put("62", state62);

    // State 63
    Map<String, String> state63 = new HashMap<>();
    state63.put("begin", "s8");
    state63.put("ALGO", "77");
    parseTable.put("63", state63);

    // State 64
    Map<String, String> state64 = new HashMap<>();
    state64.put("V_", "s10");
    state64.put("VNAME", "78");
    parseTable.put("64", state64);

    // State 65
    Map<String, String> state65 = new HashMap<>();
    state65.put("V_", "s10");
    state65.put("VNAME", "79");
    parseTable.put("65", state65);

    // State 66
    Map<String, String> state66 = new HashMap<>();
    state66.put("end", "r9");
    parseTable.put("66", state66);

    // State 67
    Map<String, String> state67 = new HashMap<>();
    state67.put(";", "r22");
    parseTable.put("67", state67);

    // State 68
    Map<String, String> state68 = new HashMap<>();
    state68.put(";", "r25");
    parseTable.put("68", state68);

    // State 69
    Map<String, String> state69 = new HashMap<>();
    state69.put(";", "r26");
    parseTable.put("69", state69);

    // State 70
    Map<String, String> state70 = new HashMap<>();
    state70.put(";", "r27");
    parseTable.put("70", state70);

    // State 71
    Map<String, String> state71 = new HashMap<>();
    state71.put("(", "s80");
    parseTable.put("71", state71);

    // State 72
    Map<String, String> state72 = new HashMap<>();
    state72.put("(", "s81");
    parseTable.put("72", state72);

    // State 73
    Map<String, String> state73 = new HashMap<>();
    state73.put(",", "s82");
    parseTable.put("73", state73);

    // State 74
    Map<String, String> state74 = new HashMap<>();
    state74.put("begin", "s8");
    state74.put("ALGO", "83");
    parseTable.put("74", state74);

    // State 75
    Map<String, String> state75 = new HashMap<>();
    state75.put("V_", "s10");
    state75.put("N_", "s41");
    state75.put("T_", "s42");
    state75.put("or", "s52");
    state75.put("and", "s53");
    state75.put("eq", "s54");
    state75.put("grt", "s55");
    state75.put("add", "s56");
    state75.put("sub", "s57");
    state75.put("mul", "s58");
    state75.put("div", "s59");
    state75.put("VNAME", "39");
    state75.put("ATOMIC", "84");
    state75.put("CONST", "40");
    state75.put("SIMPLE", "85");
    state75.put("BINOP", "86");
    parseTable.put("75", state75);

    // State 76
    Map<String, String> state76 = new HashMap<>();
    state76.put("or", "s52");
    state76.put("and", "s53");
    state76.put("eq", "s54");
    state76.put("grt", "s55");
    state76.put("add", "s56");
    state76.put("sub", "s57");
    state76.put("mul", "s58");
    state76.put("div", "s59");
    state76.put("SIMPLE", "87");
    state76.put("BINOP", "86");
    parseTable.put("76", state76);

    // State 77
    Map<String, String> state77 = new HashMap<>();
    state77.put("}", "s89");
    state77.put("EPILOG", "88");
    parseTable.put("77", state77);

    // State 78
    Map<String, String> state78 = new HashMap<>();
    state78.put(",", "s90");
    parseTable.put("78", state78);

    // State 79
    Map<String, String> state79 = new HashMap<>();
    state79.put(",", "s91");
    parseTable.put("79", state79);

    // State 80
    Map<String, String> state80 = new HashMap<>();
    state80.put("V_", "s10");
    state80.put("N_", "s41");
    state80.put("T_", "s42");
    state80.put("not", "s60");
    state80.put("sqrt", "s61");
    state80.put("or", "s52");
    state80.put("and", "s53");
    state80.put("eq", "s54");
    state80.put("grt", "s55");
    state80.put("add", "s56");
    state80.put("sub", "s57");
    state80.put("mul", "s58");
    state80.put("div", "s59");
    state80.put("VNAME", "39");
    state80.put("ATOMIC", "93");
    state80.put("CONST", "40");
    state80.put("OP", "94");
    state80.put("ARG", "92");
    state80.put("UNOP", "71");
    state80.put("BINOP", "72");
    parseTable.put("80", state80);

    // State 81
    Map<String, String> state81 = new HashMap<>();
    state81.put("V_", "s10");
    state81.put("N_", "s41");
    state81.put("T_", "s42");
    state81.put("not", "s60");
    state81.put("sqrt", "s61");
    state81.put("or", "s52");
    state81.put("and", "s53");
    state81.put("eq", "s54");
    state81.put("grt", "s55");
    state81.put("add", "s56");
    state81.put("sub", "s57");
    state81.put("mul", "s58");
    state81.put("div", "s59");
    state81.put("VNAME", "39");
    state81.put("ATOMIC", "93");
    state81.put("CONST", "40");
    state81.put("OP", "94");
    state81.put("ARG", "95");
    state81.put("UNOP", "71");
    state81.put("BINOP", "72");
    parseTable.put("81", state81);

    // State 82
    Map<String, String> state82 = new HashMap<>();
    state82.put("V_", "s10");
    state82.put("N_", "s41");
    state82.put("T_", "s42");
    state82.put("VNAME", "39");
    state82.put("ATOMIC", "96");
    state82.put("CONST", "40");
    parseTable.put("82", state82);

    // State 83
    Map<String, String> state83 = new HashMap<>();
    state83.put("else", "s97");
    parseTable.put("83", state83);

    // State 84
    Map<String, String> state84 = new HashMap<>();
    state84.put(",", "s98");
    parseTable.put("84", state84);

    // State 85
    Map<String, String> state85 = new HashMap<>();
    state85.put(",", "s99");
    parseTable.put("85", state85);

    // State 86
    Map<String, String> state86 = new HashMap<>();
    state86.put("(", "s100");
    parseTable.put("86", state86);

    // State 87
    Map<String, String> state87 = new HashMap<>();
    state87.put(")", "s101");
    parseTable.put("87", state87);

    // State 88
    Map<String, String> state88 = new HashMap<>();
    state88.put("num", "s15");
    state88.put("end", "r48");
    state88.put("void", "s16");
    state88.put("$", "r48");
    state88.put("FUNCTIONS", "103");
    state88.put("DECL", "12");
    state88.put("HEADER", "13");
    state88.put("FTYP", "14");
    state88.put("SUBFUNCS", "102");
    parseTable.put("88", state88);

    // State 89
    Map<String, String> state89 = new HashMap<>();
    state89.put("num", "r56");
    state89.put("end", "r56");
    state89.put("void", "r56");
    state89.put("$", "r56");
    parseTable.put("89", state89);

    // State 90
    Map<String, String> state90 = new HashMap<>();
    state90.put("num", "s5");
    state90.put("text", "s6");
    state90.put("VTYP", "104");
    parseTable.put("90", state90);

    // State 91
    Map<String, String> state91 = new HashMap<>();
    state91.put("V_", "s10");
    state91.put("VNAME", "105");
    parseTable.put("91", state91);

    // State 92
    Map<String, String> state92 = new HashMap<>();
    state92.put(")", "s106");
    parseTable.put("92", state92);

    // State 93
    Map<String, String> state93 = new HashMap<>();
    state93.put(",", "r30");
    state93.put(")", "r30");
    parseTable.put("93", state93);

    // State 94
    Map<String, String> state94 = new HashMap<>();
    state94.put(",", "r31");
    state94.put(")", "r31");
    parseTable.put("94", state94);

    // State 95
    Map<String, String> state95 = new HashMap<>();
    state95.put(",", "s107");
    parseTable.put("95", state95);

    // State 96
    Map<String, String> state96 = new HashMap<>();
    state96.put(",", "s108");
    parseTable.put("96", state96);

    // State 97
    Map<String, String> state97 = new HashMap<>();
    state97.put("begin", "s8");
    state97.put("ALGO", "109");
    parseTable.put("97", state97);

    // State 98
    Map<String, String> state98 = new HashMap<>();
    state98.put("V_", "s10");
    state98.put("N_", "s41");
    state98.put("T_", "s42");
    state98.put("VNAME", "39");
    state98.put("ATOMIC", "110");
    state98.put("CONST", "40");
    parseTable.put("98", state98);

    // State 99
    Map<String, String> state99 = new HashMap<>();
    state99.put("or", "s52");
    state99.put("and", "s53");
    state99.put("eq", "s54");
    state99.put("grt", "s55");
    state99.put("add", "s56");
    state99.put("sub", "s57");
    state99.put("mul", "s58");
    state99.put("div", "s59");
    state99.put("SIMPLE", "111");
    state99.put("BINOP", "86");
    parseTable.put("99", state99);

    // State 100
    Map<String, String> state100 = new HashMap<>();
    state100.put("V_", "s10");
    state100.put("N_", "s41");
    state100.put("T_", "s42");
    state100.put("VNAME", "39");
    state100.put("ATOMIC", "84");
    state100.put("CONST", "40");
    parseTable.put("100", state100);

    // State 101
    Map<String, String> state101 = new HashMap<>();
    state101.put("then", "r36");
    parseTable.put("101", state101);

    // State 102
    Map<String, String> state102 = new HashMap<>();
    state102.put("end", "s112");
    parseTable.put("102", state102);

    // State 103
    Map<String, String> state103 = new HashMap<>();
    state103.put("end", "r58");
    parseTable.put("103", state103);

    // State 104
    Map<String, String> state104 = new HashMap<>();
    state104.put("V_", "s10");
    state104.put("VNAME", "113");
    parseTable.put("104", state104);

    // State 105
    Map<String, String> state105 = new HashMap<>();
    state105.put(",", "s114");
    parseTable.put("105", state105);

    // State 106
    Map<String, String> state106 = new HashMap<>();
    state106.put(",", "r28");
    state106.put(";", "r28");
    state106.put(")", "r28");
    parseTable.put("106", state106);

    // State 107
    Map<String, String> state107 = new HashMap<>();
    state107.put("V_", "s10");
    state107.put("N_", "s41");
    state107.put("T_", "s42");
    state107.put("not", "s60");
    state107.put("sqrt", "s61");
    state107.put("or", "s52");
    state107.put("and", "s53");
    state107.put("eq", "s54");
    state107.put("grt", "s55");
    state107.put("add", "s56");
    state107.put("sub", "s57");
    state107.put("mul", "s58");
    state107.put("div", "s59");
    state107.put("VNAME", "39");
    state107.put("ATOMIC", "93");
    state107.put("CONST", "40");
    state107.put("OP", "94");
    state107.put("ARG", "115");
    state107.put("UNOP", "71");
    state107.put("BINOP", "72");
    parseTable.put("107", state107);

    // State 108
    Map<String, String> state108 = new HashMap<>();
    state108.put("V_", "s10");
    state108.put("N_", "s41");
    state108.put("T_", "s42");
    state108.put("VNAME", "39");
    state108.put("ATOMIC", "116");
    state108.put("CONST", "40");
    parseTable.put("108", state108);

    // State 109
    Map<String, String> state109 = new HashMap<>();
    state109.put(";", "r24");
    parseTable.put("109", state109);

    // State 110
    Map<String, String> state110 = new HashMap<>();
    state110.put(")", "s117");
    parseTable.put("110", state110);

    // State 111
    Map<String, String> state111 = new HashMap<>();
    state111.put(")", "s118");
    parseTable.put("111", state111);

    // State 112
    Map<String, String> state112 = new HashMap<>();
    state112.put("num", "r54");
    state112.put("end", "r54");
    state112.put("void", "r54");
    state112.put("$", "r54");
    parseTable.put("112", state112);

    // State 113
    Map<String, String> state113 = new HashMap<>();
    state113.put(",", "s119");
    parseTable.put("113", state113);

    // State 114
    Map<String, String> state114 = new HashMap<>();
    state114.put("V_", "s10");
    state114.put("VNAME", "120");
    parseTable.put("114", state114);

    // State 115
    Map<String, String> state115 = new HashMap<>();
    state115.put(")", "s121");
    parseTable.put("115", state115);

    // State 116
    Map<String, String> state116 = new HashMap<>();
    state116.put(")", "s122");
    parseTable.put("116", state116);

    // State 117
    Map<String, String> state117 = new HashMap<>();
    state117.put(",", "r34");
    state117.put(")", "r34");
    state117.put("then", "r34");
    parseTable.put("117", state117);

    // State 118
    Map<String, String> state118 = new HashMap<>();
    state118.put("then", "r35");
    parseTable.put("118", state118);

    // State 119
    Map<String, String> state119 = new HashMap<>();
    state119.put("num", "s5");
    state119.put("text", "s6");
    state119.put("VTYP", "123");
    parseTable.put("119", state119);

    // State 120
    Map<String, String> state120 = new HashMap<>();
    state120.put(")", "s124");
    parseTable.put("120", state120);

    // State 121
    Map<String, String> state121 = new HashMap<>();
    state121.put(",", "r29");
    state121.put(";", "r29");
    state121.put(")", "r29");
    parseTable.put("121", state121);

    // State 122
    Map<String, String> state122 = new HashMap<>();
    state122.put(";", "r23");
    parseTable.put("122", state122);

    // State 123
    Map<String, String> state123 = new HashMap<>();
    state123.put("V_", "s10");
    state123.put("VNAME", "125");
    parseTable.put("123", state123);

    // State 124
    Map<String, String> state124 = new HashMap<>();
    state124.put("{", "r51");
    parseTable.put("124", state124);

    // State 125
    Map<String, String> state125 = new HashMap<>();
    state125.put(",", "s126");
    parseTable.put("125", state125);

    // State 126
    Map<String, String> state126 = new HashMap<>();
    state126.put("begin", "r57");
    parseTable.put("126", state126);

    return parseTable;
  }
}
