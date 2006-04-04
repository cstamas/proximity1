// Search

function type(data)
{
	if (data.isDirectory()) {
		return "<img src='/proximity/images/directory.png'/>&nbsp;"
	} else {
		return "<img src='/proximity/images/file.png'/>&nbsp;"
	}
}

function path(data)
{
	return data.path;
}

function size(data)
{
	return data.size;
}

function lastModified(data)
{
	return data.lastModified;
}

function repositoryName(data)
{
	return data.repositoryName;
}

function doSearchAllCB(items)
{
	DWRUtil.removeAllRows('results');
    DWRUtil.addRows('results', items , [ type, path, size, lastModified, repositoryName ]);
}

function doSearchAll()
{
    proximitySupport.searchAllRepositories( $('searchAll').value, doSearchAllCB );
}

function doSearchSelected()
{
    proximitySupport.searchRepository( eval($('searchRepos').value), eval($('searchSelected').value) );
	DWRUtil.removeAllRows('results');
    DWRUtil.addRows('results', res , [ type, path, size, lastModified, repositoryName ]);
}
