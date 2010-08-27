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
						<dd>
							<summary>${bean.value}</summary>
							<detail>
								<ul>
									<li>id = ${bean.value.id}</li>
									<g:set var="domainClass" value="${grailsApplication.getDomainClass(bean.value.getClass().name)}"/>
									<g:each var="p" in="${domainClass.persistentProperties*.name}">
										<li>${p} = ${bean.value[p]}</li>
									</g:each>
								</ul>
							</detail>
						</dd>
					</g:each>
				</dl>
			</section>
		</g:if>
	</body>
</html>