package org.example.util;

import com.github.javafaker.Faker;
import org.example.entities.directory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MockPageFactory {
    public static Page generatePage(int pageId) {
        List<AttributeType> attributeTypes = Arrays.asList(AttributeType.VARCHAR, AttributeType.INTEGER, AttributeType.VARCHAR);
        Page page = new Page(pageId, attributeTypes);

        Faker faker = new Faker();

        try {
            for(int i=0; i<10 ; i++) {
                List<Attribute<?>> attributeList = Arrays.asList(
                        new VarcharAttribute(faker.name().fullName()),
                        new IntegerAttribute(faker.number().numberBetween(5, 80)),
                        new VarcharAttribute(faker.address().fullAddress())
                );
                Tuple tuple = new Tuple(attributeList);
                page.insertTuple(tuple);
            }
        }
        catch (Page.PageFullException ignored) {}

        return page;
    }

    private static List<Attribute<?>> generateAttributesList(List<AttributeType> attributeTypes, int pageId, int tupleId) {
        List<Attribute<?>> attributeList = new ArrayList<>();

        Random random = new Random();

        for (AttributeType type : attributeTypes) {
            switch (type) {
                case CHAR:
                    attributeList.add(new CharAttribute(generateRandomString(random, pageId, tupleId)));
                    break;
                case INTEGER:
                    attributeList.add(generateRandomIntegerAttribute(random));
                    break;
                case VARCHAR:
                    attributeList.add(new VarcharAttribute(generateRandomString(random, pageId, tupleId)));
                    break;
            }
        }

        return attributeList;
    }

    private static String generateRandomString(Random random, int pageId, int tupleId) {
        double lowerBound = 0.25;
        double upperBound = 0.75;
        double randomPercentage = lowerBound + (upperBound - lowerBound) * random.nextDouble();

        int length = (int) (AttributeType.CHAR.size * randomPercentage);
        String prefix = String.format("string:%d-%d__", pageId, tupleId);
        String content = generateUTF8String(length - prefix.length());

       return prefix + content;
    }

    private static IntegerAttribute generateRandomIntegerAttribute(Random random) {
        int value = random.nextInt();
        return new IntegerAttribute(value);
    }

    public static String generateUTF8String(int length) {
        return generateUTF8String(length, "", "");
    }

    private static String generateUTF8String(int length, String prefix, String suffix) {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        int desiredLength = length - prefix.getBytes().length - suffix.getBytes().length;

        while (stringBuilder.toString().getBytes().length < desiredLength) {
            char randomChar = (char) (random.nextInt(26) + 'a');  // Generates a random lowercase letter
            stringBuilder.append(randomChar);
        }

        return prefix + stringBuilder + suffix;
    }
}
