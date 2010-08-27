<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title><g:layoutTitle/></title>
		<g:layoutHead/>
		<style>
			body { 
				color: #444;
				font: 16px sans-serif; 
				*font-size: small; 
				*font: x-small; 
				line-height: 1.22; 
				margin: 10px;
			}
			
			section, header {
				display: block;
			}
			
			h1, h2 {
				border-radius: 6px;
				-moz-border-radius: 6px;
				-webkit-border-radius: 6px;
				line-height: 2;
				margin: 10px 0;
				padding: 0 10px;
			}
			h1 {
				color: #fff;
			}
			body.success h1 {
				background-color: #55b05a;
			}
			body.error h1,
			body.notfound h1 {
				background-color: #D23D24;
			}
			h2 {
				background-color: #EAEBEE;
			}
			p {
				margin: 0;
			}
			ul {
				list-style-type: none;
			}
			ul, dl {
				margin: 0;
				padding: 0;
			}
			dt {
				font-weight: bold;
				margin: 5px 0 0 0;
			}
			dfn {
				display: inline-block;
				font-style: normal;
				font-weight: bold;
				margin-right: 5px;
				min-width: 50px;
			}
			dfn:after,
			dt:after {
				content: ":";
			}
		</style>
	</head>
	<body class="${pageProperty(name: 'body.class')}">
		<g:layoutBody/>
		<section id="domain-object-counts">
			<h2>Domain object counts:</h2>
			<ul>
				<g:each var="domainClass" in="${grailsApplication.domainClasses}">
					<li><dfn>${domainClass.name}</dfn>${domainClass.clazz.count()}</li>
				</g:each>
			</ul>
		</section>
	</body>
</html>