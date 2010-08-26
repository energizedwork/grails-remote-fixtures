<!DOCTYPE html>
<h1>Fixture '${params.fixture}' loaded</h1>
<h2>Fixture data&hellip;</h2>
<ul class="fixture">
	<g:each var="bean" in="${fixture}">
		<li>${bean.key}: ${bean.value}</li>
	</g:each>
</ul>
<h2>Domain object counts&hellip;</h2>
<ul class="domain-object-counts">
	<g:each var="domainClass" in="${grailsApplication.domainClasses}">
		<li>${domainClass.name}: ${domainClass.clazz.count()}</li>
	</g:each>
</ul>