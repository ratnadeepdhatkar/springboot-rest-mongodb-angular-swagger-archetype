app.controller('DocumentsController', function ($rootScope, $scope, DocumentService) {
    $scope.init = function() {
        DocumentService.getDocumentList().then(
            function success(response) {
                $scope.documents = response;
            },
            function error () {
                console.log("DOCUMENTS_GET_FAILURE");
            }
        );
    };

    $scope.upload = function() {
        DocumentService.upload().then(
            function success(response) {
                $scope.documents.push(response);
            },
            function error() {
                console.log("DOCUMENT_POST_FAILURE");
            }
        );
    };


    $scope.remove = function(index) {
        var documentToDelete = $scope.documents[index];
        console.log("Deleting file " + documentToDelete.id + "...");
        DocumentService.deleteDocument(documentToDelete.id).then(
            function success(response) {
                console.log("Document with ID " + documentToDelete.id + " deleted.\n" + response);
                $scope.documents.splice(index, 1);
            },
            function error() {
                console.log("DOCUMENT_REMOVE_FAILURE");
            }
        );
    };
});