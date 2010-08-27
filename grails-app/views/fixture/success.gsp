<!DOCTYPE html>
<html>
	<head>
		<title>Fixture '${params.fixture}' loaded</title>
	</head>
	<body class="success">
		<header><h1>Fixture <em>${params.fixture}</em> loaded</h1></header>
		<g:if test="${fixture}">
			<section>
				<h2>Fixture data:</h2>
				<dl>
					<g:each var="bean" in="${fixture}">
						<dt>${bean.key} (${bean.value.getClass().simpleName})</dt>
						<dd>${bean.value}</dd>
					</g:each>
				</dl>
			</section>
		</g:if>
	</body>
</html>