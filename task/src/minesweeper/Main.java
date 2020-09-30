package minesweeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static int fieldSize;
    public static String[][] minefield;
    public static int mineCount;
    public static ArrayList<Integer[]> mineCoords;
    public static boolean firstMove;

    public static void main(String[] args) {
        fieldSize = 9;
        minefield = new String[fieldSize][fieldSize];
        mineCount = 0;
        mineCoords = new ArrayList<>();
        firstMove = true;

        setField(setMineQty());
        printField();
        cycle();
    }

    public static boolean isInList(
            final List<Integer[]> list, final Integer[] candidate) {

        return list.stream().anyMatch(a -> Arrays.equals(a, candidate));
        //  ^-- or you may want to use .parallelStream() here instead
    }

    public static int setMineQty(){
        Scanner s = new Scanner(System.in);
        System.out.print("How many mines do you want on the field? ");
        mineCount = s.nextInt();
        return mineCount;
    }

    public static void setField(int mineQty) {
        for (int i = 0; i < fieldSize; i++){
            for (int j = 0; j < 9; j++){
                minefield[i][j] = ".";
            }
        }
        placeMines(mineQty);
//        setProximity();
    }

    public static void placeMines(int mineQty){
        for (int k = 0; k < mineQty; k++) {
            int iMine = (int) (Math.random() * 9);
            int jMine = (int) (Math.random() * 9);

            if (!isInList(mineCoords, new Integer[]{iMine, jMine})) {
//            if (minefield[iMine][jMine] != "*") {
//                minefield[iMine][jMine] = "*";
                mineCoords.add(new Integer[] {iMine, jMine});
            } else {
                placeMines(mineQty - k);
                break;
            }
        }

    }

    private static void cycle() {
        while (mineCount > 0) {
            String[] xyCombo = setDelete();
            switch (xyCombo[2]) {
                case "free":
                    checkMines(Integer.parseInt(xyCombo[0]) - 1, Integer.parseInt(xyCombo[1]) - 1);
                    break;
                case "mine":
                    flagMines(Integer.parseInt(xyCombo[0]) - 1, Integer.parseInt(xyCombo[1]) - 1);
                    break;
            }
            firstMove = false;
            printField();
        }
        if (mineCount == 0) {
            System.out.println("Congratulations! You found all mines!");
        } else {
            System.out.println("You stepped on a mine and failed!");
        }
    }

    private static void checkMines(int x, int y) {
        Integer[] xy = new Integer[2];
        xy[0] = y;
        xy[1] = x;
        if (isInList(mineCoords, xy) && firstMove) {
            mineCoords.removeIf(i -> i == xy);
            placeMines(1);
        }
        for (Integer[] ab : mineCoords) {
            if (Arrays.equals(ab, xy)) {
                boom();
            } else {
//                System.out.println("????????????");
                freeMines(xy[0], xy[1]);
            }
        }
    }

    private static void freeMines(int y, int x) {
        if (y >= 0 && y < fieldSize && x >= 0 && x < fieldSize) {
            int checkProx = checkProximity(y, x);
            if (checkProx == 0 && minefield[y][x].equals(".")) {
                minefield[y][x] = "/";
//                System.out.println("marked: "+ x + " | " + y);
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if (!(i == 0 & j == 0)) {
//                            System.out.println("\t "+i + " | " + j);
                            int a = x + i;
                            int b = y + j;
                            if (b >= 0 && b < fieldSize && a >= 0 && a < fieldSize) {
//                                System.out.println("\t\t : checkMines: " + a + ", " + b);
                                checkMines(a, b);
                            }
                        }
                    }
                }
            } else if (checkProx == 0) {
                minefield[y][x] = "/";
            } else if (!minefield[y][x].equals("X")) {
//                System.out.println("checkProxChanged: ("+checkProx+")"+ x + " | " + y);
                minefield[y][x] = checkProx + "";
            }
        }
    }

    private static int checkProximity(int y, int x) {
        int proxVal = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Integer[] testArr = new Integer[2];
                testArr[0] = y + i;
                testArr[1] = x + j;
                for (Integer[] ab : mineCoords) {
//                    System.out.println(Arrays.toString(testArr));
                    if (Arrays.equals(ab, testArr)) {
                        proxVal++;
                    }
                }
            }
        }
        return proxVal;
    }

    private static String[] setDelete() {
//        System.out.println("mines: " + mineCount);
//        System.out.println("[y, x] "+Arrays.deepToString(mineCoords.toArray()));
        System.out.print("Set/unset mines marks or claim a cell as free: ");
        Scanner scan = new Scanner(System.in);
        String[] rtnArr = scan.nextLine().split(" ");
//        int x = Integer.parseInt(String.valueOf(xy.[0]) - 1;
//        int y = Integer.parseInt(String.valueOf(xy[1])) - 1;
//        int[] rtnArr = new int[]{x, y};
        return rtnArr;
    }

    public static void setProximity(){
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
//                if (minefield[i][j] != "*") {
                for (Integer[] item : mineCoords) {
                    if (!Arrays.equals(item, new Integer[]{i, j})) {
                        int xCount = 0;
                        for (int x = -1; x < 2; x++) {
                            for (int y = -1; y < 2; y++) {
                                if (i - x >= 0 && i - x < fieldSize && j - y >= 0 && j - y < fieldSize) {
//                                if (minefield[i-x][j-y] == "*") {
                                    if (Arrays.equals(item, new Integer[]{(i - x), (j - y)})) {
                                        xCount++;
                                    }
                                }
                            }
                        }
                        if (xCount > 0 && !Arrays.equals(item, new Integer[]{i, j})) {
                            if (minefield[i][j].equals(".")) {
                                minefield[i][j] = xCount + "";
                            } else {
                                minefield[i][j] = xCount + Integer.parseInt(minefield[i][j]) + "";
                            }
                        }
                    }
                }
            }
        }
    }

    public static void boom() {
        for (Integer[] ab : mineCoords) {
            minefield[ab[0]][ab[1]] = "X";
        }
        mineCount = -1;
    }

    public static void flagMines(int x, int y){
        if (minefield[y][x].equals("*")) {
            minefield[y][x] = ".";
            mineCount--;
        } else if (minefield[y][x].equals(".")) {
            minefield[y][x] = "*";
            if (isInList(new Integer[]{y, x})) {
                mineCount--;
//            } else {
//                mineCount++;
            }
        } else{
            System.out.println("There is a number here!");
        }
    }

    public static boolean isInList(Integer[] arr){
        return mineCoords.stream().anyMatch(a -> Arrays.equals(a, arr));
    }

    public static void printField(){
        System.out.println("\n │123456789│\n—│—————————│");
        for (int i = 0; i < fieldSize; i++) {
            System.out.println((i+1)+"|" + Arrays.toString(minefield[i]).replaceAll(",| ", "").replaceAll("\\[|\\]", "") +"|" );
        }
        System.out.println("—│—————————│");
    }

    public static void printFieldMasked(){
        System.out.println("\n │123456789│\n—│—————————│");
        for (int i = 0; i < fieldSize; i++) {
            System.out.println((i+1)+"|" + Arrays.toString(minefield[i]).replaceAll(",| ", "").replaceAll("\\[|\\]", "").replaceAll("\\*", ".") +"|" );
        }
        System.out.println("—│—————————│");
    }
}
