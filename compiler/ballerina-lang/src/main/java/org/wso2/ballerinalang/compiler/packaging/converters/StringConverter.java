package org.wso2.ballerinalang.compiler.packaging.converters;

import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.repository.CompilerInput;

import java.util.stream.Stream;

/**
 * Provide functions need to covert a patten to a string.
 */
public class StringConverter implements Converter<String> {

    @Override
    public String combine(String a, String b) {
        return (a + "/" + b);
    }

    @Override
    public Stream<String> latest(String s, PackageID packageID) {
        return Stream.of(s + "/semVer(*)");
    }

    @Override
    public Stream<String> expandBalWithTest(String t) {
        return Stream.of(t + "/**~test~resources/*.bal");
    }

    @Override
    public Stream<String> expandBal(String s) {
        return Stream.of(s + "/**~resources/*.bal");
    }

    @Override
    public String start() {
        return "$";
    }

    @Override
    public Stream<CompilerInput> finalize(String s, PackageID id) {
        throw new UnsupportedOperationException();
    }
}
