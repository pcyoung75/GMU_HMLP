/*
 * HML Core
 * Copyright (C) 2017 Cheol Young Park
 * 
 * This file is part of HML Core.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mebn_rm.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import util.SortableValueMap;
import util.TempMathFunctions;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files; 
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List; 

public class ExcelCSV {
    public static final void createCSV(String file) {
        try {
        	String filename = String.valueOf(file) + ".csv";
//        	Files.deleteIfExists(Paths.get(filename));
        	CSVWriter writer1 = new CSVWriter((Writer)new FileWriter(filename, true));
            writer1.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static final void writeCSV(String file, ArrayList<Double> listD) {
        String[] listS = new String[listD.size()];
        int i = 0;
        for (Double d : listD) {
            listS[i++] = d.toString();
        }
        ExcelCSV.writeCSV(file, listS);
    }

    public static final void writeCSV2(String file, ArrayList<String> listD) {
        String[] listS = new String[listD.size()];
        int i = 0;
        for (String s : listD) {
            listS[i++] = s;
        }
        ExcelCSV.writeCSV(file, listS);
    }

    public static final /* varargs */ void writeCSV(String file, String ... textList) {
        try {
            CSVReader reader = new CSVReader((Reader)new FileReader(String.valueOf(file) + ".csv"));
            List<String[]> allElements = reader.readAll();
            allElements.add(textList);
            CSVWriter writer = new CSVWriter((Writer)new FileWriter(String.valueOf(file) + ".csv"));
            for (String[] strList : allElements) {
                writer.writeNext(strList);
            }
            writer.close();
            reader.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static final void writeAVG_STD_CSV(String file) {
        try {
            DoubleArrayList list;
            CSVReader reader1 = new CSVReader((Reader)new FileReader(String.valueOf(file) + ".csv"));
            List<String[]> allElements = reader1.readAll();
            CSVReader reader = new CSVReader((Reader)new FileReader(String.valueOf(file) + ".csv"));
            String[] column = reader.readNext();
            int size = column.length;
            SortableValueMap<Integer, DoubleArrayList> map = new SortableValueMap<Integer, DoubleArrayList>();
            int i = 0;
            while (i < column.length) {
                list = new DoubleArrayList();
                map.put(i, list);
                ++i;
            }
            while (column != null) {
                i = 0;
                while (i < column.length) {
                    list = (DoubleArrayList)map.get(i);
                    if (TempMathFunctions.isNum(column[i])) {
                        list.add(Double.valueOf(column[i]).doubleValue());
                    }
                    ++i;
                }
                column = reader.readNext();
            }
            String[] averages = new String[size];
            String[] stds = new String[size];
            int i2 = 0;
            while (i2 < size) {
                DoubleArrayList list2 = (DoubleArrayList)map.get(i2);
                if (list2 != null && list2.size() > 0) {
                    Double mean = Descriptive.mean((DoubleArrayList)list2);
                    Double var = Descriptive.covariance((DoubleArrayList)list2, (DoubleArrayList)list2);
                    averages[i2] = mean.toString();
                    stds[i2] = var.toString();
                }
                ++i2;
            }
            allElements.add(averages);
            allElements.add(stds);
            CSVWriter writer = new CSVWriter((Writer)new FileWriter(String.valueOf(file) + ".csv"));
            for (String[] strList : allElements) {
                writer.writeNext(strList);
            }
            writer.close();
            reader.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {
    }
}

