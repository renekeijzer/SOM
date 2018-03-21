package general.normalize;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import general.data.BasicData;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;


public class DataSet {


    private final List<Object[]> data = new ArrayList<>();

    private String[] headers;


    private final NumberFormat numberFormatter = NumberFormat.getInstance(Locale.US);


    public DataSet(final String[] theHeaders) {
        this.headers = theHeaders;
    }


    private double convertNumeric(final Object[] obj, final int column) {
        final double x;
        if (obj[column] instanceof Double) {
            x = (Double) obj[column];
        } else {
            try {
                x = this.numberFormatter.parse(obj[column].toString()).doubleValue();
                obj[column] = x;
            } catch (ParseException e) {
                throw new Error(e);
            }
        }

        return x;
    }


    public static DataSet load(final File filename) {
        try {
            final FileInputStream fis = new FileInputStream(filename);
            final DataSet ds = load(fis);
            fis.close();
            return ds;
        } catch (IOException ex) {
            throw (new Error(ex));
        }
    }


    public static DataSet load(final InputStream is) {
        final DataSet result;

        try {
            final Reader reader = new InputStreamReader(is);
            final CSVReader csv = new CSVReader(reader);

            final String[] headers = csv.readNext();

            result = new DataSet(headers);

            String[] nextLine;
            while ((nextLine = csv.readNext()) != null) {
                if (nextLine.length <= 1) {
                    continue;
                } else if (nextLine.length != result.getHeaderCount()) {
                    throw new Error("Found a CSV line with "
                            + nextLine.length + " columns, when expecting " + result.getHeaderCount());
                }
                final Object[] obj = new Object[result.getHeaderCount()];
                System.arraycopy(nextLine, 0, obj, 0, nextLine.length);
                result.add(obj);
            }
            csv.close();
        } catch (IOException ex) {
            throw (new Error(ex));
        }

        return result;
    }

    public static void save(final File filename, final DataSet ds) {
        try {
            final FileOutputStream fos = new FileOutputStream(filename);
            save(fos, ds);
            fos.close();
        } catch (IOException ex) {
            throw (new Error(ex));
        }
    }

    public static void save(final OutputStream os, final DataSet ds) {
        try {
            final Writer writer = new OutputStreamWriter(os);
            final CSVWriter csv = new CSVWriter(writer);

            csv.writeNext(ds.getHeaders());
            final String[] items2 = new String[ds.getHeaderCount()];

            for (final Object[] item : ds.getData()) {
                for (int i = 0; i < ds.getHeaderCount(); i++) {
                    items2[i] = item[i].toString();
                }
                csv.writeNext(items2);
            }
            csv.close();
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }


    public int getHeaderCount() {
        return this.headers.length;
    }

    public String[] getHeaders() {
        return this.headers;
    }


    public void add(final Object[] row) {
        this.data.add(row);
    }

    public List<Object[]> getData() {
        return this.data;
    }

    public double getMax(final int column) {
        double result = Double.NEGATIVE_INFINITY;

        for (final Object[] obj : this.data) {
            result = Math.max(result, convertNumeric(obj, column));
        }

        return result;
    }

    public double getMean(final int column) {
        double sum = 0;
        int count = 0;

        for (final Object[] obj : this.data) {
            if(!DataSet.isMissing(obj[column].toString())) {
                sum += convertNumeric(obj, column);
                count++;
            }
        }

        return sum/count;
    }

    public double getStandardDeviation(final int column) {
        double mean = getMean(column);
        double sum = 0;
        int count = 0;

        for (final Object[] obj : this.data) {
            if(!DataSet.isMissing(obj[column].toString())) {
                double delta = mean - convertNumeric(obj, column);
                sum += delta*delta;
                count++;
            }
        }

        return Math.sqrt(sum/count);
    }


    public static boolean isMissing(String str) {
        return( str.equals("?") || str.trim().equals("") || str.trim().toUpperCase().equals("NA")
                || str.trim().toUpperCase().equals("NULL"));
    }

    public double getMin(final int column) {
        double result = Double.POSITIVE_INFINITY;

        for (final Object[] obj : this.data) {
            result = Math.min(result, convertNumeric(obj, column));
        }

        return result;
    }

    public void normalizeRange(final int column, final double dataLow, final double dataHigh, final double normalizedLow, final double normalizedHigh) {
        for (final Object[] obj : this.data) {
            final double x = convertNumeric(obj, column);

            obj[column] = ((x - dataLow)
                    / (dataHigh - dataLow))
                    * (normalizedHigh - normalizedLow) + normalizedLow;
        }
    }

    public void normalizeRange(final int column, final double normalizedLow, final double normalizedHigh) {
        final double dataLow = getMin(column);
        final double dataHigh = getMax(column);
        normalizeRange(column, dataLow, dataHigh, normalizedLow, normalizedHigh);
    }

    public void normalizeZScore(final int column) {
        final double standardDeviation =  getStandardDeviation(column);
        final double mean = getMean(column);

        for (final Object[] obj : this.data) {
            if(isMissing(obj[column].toString())) {
                obj[column] = 0; // Place at mean
            } else {
                double x = convertNumeric(obj, column);
                obj[column] = (x - mean)/standardDeviation;
            }
        }
    }

    public void deNormalizeRange(final int column, final double dataLow, final double dataHigh, final double normalizedLow, final double normalizedHigh) {
        for (final Object[] obj : this.data) {
            final double x = convertNumeric(obj, column);

            obj[column] = ((dataLow - dataHigh) * x - normalizedHigh
                    * dataLow + dataHigh * normalizedLow)
                    / (normalizedLow - normalizedHigh);
        }
    }

    public void normalizeReciprocal(final int column) {
        for (final Object[] obj : this.data) {
            final double x = convertNumeric(obj, column);
            obj[column] = 1 / x;
        }
    }

    public void deNormalizeReciprocal(final int column) {
        normalizeReciprocal(column);
    }

    public Map<String, Integer> enumerateClasses(final int column) {
        // determine classes
        final Set<String> classes = new HashSet<>();
        for (final Object[] obj : this.data) {
            classes.add(obj[column].toString());
        }
        // assign numeric values to each class
        final Map<String, Integer> result = new HashMap<>();
        int index = 0;
        for (final String className : classes) {
            result.put(className, index++);
        }

        return result;
    }

    public Map<String, Integer> encodeNumeric(final int column) {
        final Map<String, Integer> classes = enumerateClasses(column);

        for (final Object[] obj : this.data) {
            final int index = classes.get(obj[column].toString());
            obj[column] = index;
        }

        return classes;
    }


    public Map<String, Integer> encodeOneOfN(final int column) {
        return encodeOneOfN(column, 0, 1);
    }


    public Map<String, Integer> encodeOneOfN(final int column, final double offValue, final double onValue) {
        // remember the column name
        final String name = this.headers[column];

        // make space for it
        final Map<String, Integer> classes = enumerateClasses(column);
        insertColumns(column + 1, classes.size() - 1);

        // perform the 1 of n encode
        for (final Object[] obj : this.data) {
            final int index = classes.get(obj[column].toString());
            final int classCount = classes.size();

            for (int i = 0; i < classCount; i++) {
                obj[column + i] = (i == index) ? onValue : offValue;
            }
        }

        // name the new columns
        for (int i = 0; i < classes.size(); i++) {
            this.headers[column + i] = name + "-" + i;
        }

        return classes;
    }

    public Map<String, Integer> encodeEquilateral(final int column) {
        return encodeEquilateral(column, 0, 1);
    }

    public Map<String, Integer> encodeEquilateral(final int column, final double offValue, final double onValue) {
        // remember the column name
        final String name = this.headers[column];

        // make space for it
        final Map<String, Integer> classes = enumerateClasses(column);
        final int classCount = classes.size();
        insertColumns(column + 1, classCount - 1);

        // perform the equilateral
        final Equilateral eq = new Equilateral(classCount, offValue, onValue);

        for (final Object[] obj : this.data) {
            final int index = classes.get(obj[column].toString());

            final double[] encoded = eq.encode(index);

            for (int i = 0; i < classCount - 1; i++) {
                obj[column + i] = encoded[i];
            }
        }

        // name the new columns
        for (int i = 0; i < classes.size(); i++) {
            this.headers[column + i] = name + "-" + i;
        }

        return classes;
    }

    public int size() {
        return this.data.size();
    }

    public void appendColumns(final int count) {

        // add the headers
        final String[] newHeaders = new String[getHeaderCount() + count];
        System.arraycopy(this.headers, 0, newHeaders, 0, getHeaderCount());

        for (int i = 0; i < count; i++) {
            newHeaders[i + getHeaderCount()] = "new";
        }

        this.headers = newHeaders;

        // add the data
        for (int rowIndex = 0; rowIndex < size(); rowIndex++) {
            final Object[] originalRow = this.data.get(rowIndex);
            final Object[] newRow = new Object[getHeaderCount()];
            System.arraycopy(originalRow, 0, newRow, 0, originalRow.length);
            for (int i = 0; i < count; i++) {
                newRow[getHeaderCount() - 1 - i] = (double) 0;
            }
            this.data.remove(rowIndex);
            this.data.add(rowIndex, newRow);
        }
    }

    public void insertColumns(final int column, final int columnCount) {
        // create space for new columns
        appendColumns(columnCount);

        // insert headers
        System.arraycopy(this.headers, column + 1 - columnCount, this.headers, column + 1, getHeaderCount() - 1 - column);

        // mark new columns headers
        for (int i = 0; i < columnCount; i++) {
            this.headers[column + i] = "new";
        }

        for (final Object[] obj : this.data) {
            // insert columns
            System.arraycopy(obj, column + 1 - columnCount, obj, column + 1, getHeaderCount() - 1 - column);

            // mark new columns
            for (int i = 0; i < columnCount; i++) {
                obj[column + i] = (double) 0;
            }
        }


    }


    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof DataSet)) {
            return false;
        }

        final DataSet otherSet = (DataSet) other;

        // do the basic sizes match

        if (getHeaderCount() != otherSet.getHeaderCount()) {
            return false;
        }

        if (size() != otherSet.size()) {
            return false;
        }

        // do the headers match?
        for (int i = 0; i < getHeaderCount(); i++) {
            if (!this.headers[i].equals(otherSet.getHeaders()[i])) {
                return false;
            }
        }

        // does the data match?
        for (int i = 0; i < size(); i++) {
            final Object[] row1 = this.data.get(i);
            final Object[] row2 = ((DataSet) other).getData().get(i);

            for (int j = 0; j < getHeaderCount(); j++) {
                if (!row1[j].equals(row2[j])) {
                    return false;
                }
            }
        }


        return true;
    }

    public List<BasicData> extractUnsupervisedLabeled(final int labelIndex) {
        final List<BasicData> result = new ArrayList<>();

        final int dimensions = getHeaderCount() - 1;

        for (int rowIndex = 0; rowIndex < size(); rowIndex++) {
            final Object[] raw = this.data.get(rowIndex);
            final BasicData row = new BasicData(dimensions, 0, raw[labelIndex].toString());

            int colIndex = 0;
            for (int rawColIndex = 0; rawColIndex < getHeaderCount(); rawColIndex++) {
                if (rawColIndex != labelIndex) {
                    row.getInput()[colIndex++] = convertNumeric(raw, rawColIndex);
                }
            }

            result.add(row);
        }

        return result;
    }

    public List<BasicData> extractSupervised(final int inputBegin, final int inputCount, final int idealBegin, final int idealCount) {
        final List<BasicData> result = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < size(); rowIndex++) {
            final Object[] raw = this.data.get(rowIndex);
            final BasicData row = new BasicData(inputCount, idealCount);

            for (int i = 0; i < inputCount; i++) {
                row.getInput()[i] = convertNumeric(raw, inputBegin + i);
            }

            for (int i = 0; i < idealCount; i++) {
                row.getIdeal()[i] = convertNumeric(raw, idealBegin + i);
            }

            result.add(row);
        }

        return result;

    }

    public void deleteUnknowns() {
        int rowIndex = 0;
        while (rowIndex < this.data.size()) {
            final Object[] row = this.data.get(rowIndex);
            boolean remove = false;
            for (final Object aRow : row) {
                if (aRow.toString().equals("?")) {
                    remove = true;
                    break;
                }
            }

            if (remove) {
                this.data.remove(rowIndex);
            } else {
                rowIndex++;
            }
        }
    }

    public void deleteColumn(final int col) {
        final String[] headers2 = new String[this.headers.length - 1];

        // first, remove the header
        int h2Index = 0;
        for (int i = 0; i < this.headers.length; i++) {
            if (i != col) {
                headers2[h2Index++] = this.headers[i];
            }
        }
        this.headers = headers2;

        // now process the data
        int rowIndex = 0;
        for (final Object[] row : this.data) {
            final Object[] row2 = new Object[this.headers.length];
            int r2Index = 0;
            for (int i = 0; i <= this.headers.length; i++) {
                if (i != col) {
                    row2[r2Index++] = row[i];
                }
            }
            this.data.set(rowIndex++, row2);
        }
    }

    public void replaceColumn(final int columnIndex, final double searchFor, final double replaceWith, final double others) {
        for (final Object[] row : this.data) {
            final double d = convertNumeric(row, columnIndex);
            if (Math.abs(d - searchFor) < 0.0001) {
                row[columnIndex] = replaceWith;
            } else {
                row[columnIndex] = others;
            }

        }
    }

    public List<String> columnAsList(int columnIndex) {
        List<String> result = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < size(); rowIndex++) {
            final Object[] raw = this.data.get(rowIndex);
            result.add(raw[columnIndex].toString());
        }

        return result;
    }
}