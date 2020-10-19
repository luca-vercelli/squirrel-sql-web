'use strict'
var gulp = require('gulp');
var destfolder = '../webapp';
const del = require('del');

gulp.task('dist', function () {
    return gulp.src(['index.html', 'assets/**/*', 'main/**/*'], { "base" : "." })
        .pipe(gulp.dest(destfolder));
});

gulp.task('clean:dist', function () {
    return del([
      destfolder + '/assets', destfolder + '/main', destfolder + '/*.html*'
    ]);
});