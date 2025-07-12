package net.heckntarnation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        //Read file from argument
        File file = new File(args[0]);
        BufferedReader fr = new BufferedReader(new FileReader(file));
        List<String> lines = fr.lines().toList();
        ArrayList<TokenParamed> tokens = new ArrayList<TokenParamed>();
        //Parse and tokenize
        for(String line : lines){
            String[] splitLine = line.split(" ");
            TokenParamed paramed = new TokenParamed(tokenize(splitLine[0]));
            if(paramed.token == null){continue;}
            for(int i = 1; i < splitLine.length; i++) {
                paramed.setParam(i-1, splitLine[i]);
            }
            tokens.add(paramed);
        }
        //Run
        run(tokens);
    }

    public static Token tokenize(String opCode){
        switch(opCode){
            case "|": {
                break;
            }
            case "#": {
                return Token.GRID_SIZE;
            }
            case "?": {
                return Token.OUTPUT_MODE;
            }
            case "+": {
                return Token.ADD;
            }
            case "-": {
                return Token.SUB;
            }
            case "*": {
                return Token.MUL;
            }
            case "/": {
                return Token.DIV;
            }
            case "%": {
                return Token.MOD;
            }
            case "^": {
                return Token.MOVE_UP;
            }
            case "v": {
                return Token.MOVE_DOWN;
            }
            case "<": {
                return Token.MOVE_LEFT;
            }
            case ">": {
                return Token.MOVE_RIGHT;
            }
            case "&<": {
                return Token.LOAD;
            }
            case ">&": {
                return Token.UNLOAD;
            }
            case "=&": {
                return Token.INPUT_LOAD;
            }
            case "@": {
                return Token.LABEL;
            }
            case "->": {
                return Token.JUMP;
            }
            case "->=": {
                return Token.JUMP_EQUAL;
            }
            case "-><": {
                return Token.JUMP_LESS;
            }
            case "<->": {
                return Token.REVERSE;
            }
            case ";": {
                return Token.HALT;
            }
            case "~": {
                return Token.EXIT;
            }
        }
        return null;
    }

    public static void run(ArrayList<TokenParamed> tokens) throws InterruptedException {
        TokenParamed gridSize = tokens.remove(0);
        short[][] grid = new short[Integer.parseInt(gridSize.params[0])][Integer.parseInt(gridSize.params[1])];
        boolean num_output = true;
        int x = 0; int y = 0;
        int token_index = 0;
        short register = 0;
        HashMap<String, Integer> labels = new HashMap<String,Integer>();
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        while(run){
            TokenParamed token = tokens.get(token_index);
            System.out.println("[" + x + "]" + "["+y+"] " + token);
            switch(token.token){
                case OUTPUT_MODE:{
                    num_output = token.params[0].equalsIgnoreCase("num");
                    break;
                }
                case ADD:{
                    grid[y][x] += Short.parseShort(token.params[0]);
                    break;
                }
                case SUB:{
                    grid[y][x] -= Short.parseShort(token.params[0]);
                    break;
                }
                case MUL:{
                    grid[y][x] *= Short.parseShort(token.params[0]);
                    break;
                }
                case DIV:{
                    grid[y][x] /= Short.parseShort(token.params[0]);
                    //THIS SHOULD BE AN ILLEGAL INSTRUCTION IN ASSEMBLY-LIKE LANGUAGES! DIVISION IN SINFUL!
                    break;
                }
                case MOD:{
                    grid[y][x] %= Short.parseShort(token.params[0]);
                    //SINFUL!!!
                    break;
                }
                case LOAD:{
                    register = grid[y][x];
                    break;
                }
                case UNLOAD:{
                    grid[y][x] = token.params[0] == null ? register : Short.parseShort(token.params[0]);
                    break;
                }
                case INPUT_LOAD:{
                    register = Short.parseShort(scanner.nextLine());
                    break;
                }
                case MOVE_LEFT:{
                    try{
                        x--;
                    }catch(ArrayIndexOutOfBoundsException e){
                        x++;
                        grid[y][x] = 255;
                    }
                    break;
                }
                case MOVE_RIGHT:{
                    try{
                        x++;
                    }catch(ArrayIndexOutOfBoundsException e){
                        x--;
                        grid[y][x] = 255;
                    }
                    break;
                }
                case MOVE_DOWN:{
                    try{
                        y--;
                    }catch(ArrayIndexOutOfBoundsException e){
                        y++;
                        grid[y][x] = 255;
                    }
                    break;
                }
                case MOVE_UP:{
                    try{
                        y++;
                    }catch(ArrayIndexOutOfBoundsException e){
                        y--;
                        grid[y][x] = 255;
                    }
                    break;
                }
                case LABEL:{
                    labels.put(token.params[0], token_index);
                    break;
                }
                case JUMP:{
                    token_index = labels.get(token.params[0]);
                    break;
                }
                case JUMP_EQUAL:{
                    if(Short.parseShort(token.params[1]) == grid[y][x]) {
                        token_index = labels.get(token.params[0]);
                    }
                    break;
                }
                case JUMP_LESS:{
                    if(grid[y][x] < Short.parseShort(token.params[1])) {
                        token_index = labels.get(token.params[0]);
                    }
                    break;
                }
                case REVERSE:{
                    StringBuilder sb = new StringBuilder(grid[y][x] + "");
                    sb.reverse();
                    grid[y][x] = Short.parseShort(sb.toString());
                    break;
                }
                case HALT:{
                    Thread.sleep(Long.parseLong(token.params[0]));
                    break;
                }
                case EXIT:{
                    run = false;
                    break;
                }
            }
            token_index++;
        }
        output(grid, num_output);
    }

    public static void output(short[][] grid, boolean num_output){
        for(int y = 0; y < grid.length; y++){
            for(int x = 0; x < grid[y].length; x++){
                System.out.print(num_output ? String.format("%08d", grid[y][x] & 0xFF) + " " : Character.toString((char)grid[y][x]) + " ");
            }
            System.out.print("\n");
        }
    }

    public enum Token{
        GRID_SIZE, OUTPUT_MODE,
        ADD, SUB, MUL, DIV, MOD,
        LOAD, UNLOAD, INPUT_LOAD,
        MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN,
        LABEL, JUMP, JUMP_EQUAL, JUMP_LESS,
        REVERSE,
        HALT,
        EXIT,

        Token();
    }
}