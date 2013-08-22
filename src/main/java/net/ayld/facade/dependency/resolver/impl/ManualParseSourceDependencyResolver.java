package net.ayld.facade.dependency.resolver.impl;

import java.io.IOException;
import java.util.Set;

import net.ayld.facade.dependency.resolver.SourceDependencyResolver;
import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.SourceFile;
import net.ayld.facade.util.Tokenizer;
import net.ayld.facade.util.annotation.ThreadSafe;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

@ThreadSafe
public class ManualParseSourceDependencyResolver implements SourceDependencyResolver{

	@Override
	public Set<ClassName> resolve(SourceFile source) throws IOException {
		
		final String sourceFileContent = Resources.toString(source.physicalFile().toURI().toURL(), Charsets.UTF_8);
		
		// we can somehow select only lines starting with import so we don't need to iterate over every single line
		final Set<ClassName> result = Sets.newHashSet();
		for (String line : Splitter.on("\n").split(sourceFileContent)) {
			
			if (line.startsWith(SourceFile.IMPORT_KEYWOD)) {
				
				final String dependency = Tokenizer.delimiter(" ").tokenize(line).lastToken().replaceAll(";", "");
				result.add(new ClassName(dependency));
			}
		}
		
		return ImmutableSet.copyOf(result);
	}

	@Override
	public Set<ClassName> resolve(Set<SourceFile> sources) throws IOException {
		final Set<ClassName> result = Sets.newHashSet();
		
		for (SourceFile source : sources) {
			result.addAll(resolve(source));
		}
		
		return result;
	}
}
