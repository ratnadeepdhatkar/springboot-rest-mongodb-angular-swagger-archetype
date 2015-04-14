app.service('DocumentService', function ($http, $q) {
    this.getDocumentList = function () {
        var deferred = $q.defer();

        $http.get('api/documents')
            .success(function (response) {
                deferred.resolve(response);
            })
            .error(function () {
                deferred.reject();
            });
        return deferred.promise;
    };

    this.upload = function () {
        var fileInput = document.getElementById('file');
        if (fileInput === null ||
            fileInput === undefined ||
            fileInput.files.length <= 0 ||
            fileInput.files[0] === null ||
            fileInput.files[0] === undefined
        ) {
            console.log("Error: no files in input");
            return;
        }
        var form = new FormData();
        form.append("file", fileInput.files[0]);
        var deferred = $q.defer();

        $http.
            post("api/documents", form, {
                headers: {
                    "Content-Type": undefined
                },
                transformRequest: angular.identity
            }).
            success(function (response) {
                deferred.resolve(response);
            }).
            error(function () {
                deferred.reject();
            });
        return deferred.promise;
    };

    this.deleteDocument = function (id) {
        var deferred = $q.defer();

        $http.delete('api/documents/' + id)
            .success(function (response) {
                deferred.resolve(response);
            })
            .error(function () {
                deferred.reject();
            });
        return deferred.promise;
    };
});