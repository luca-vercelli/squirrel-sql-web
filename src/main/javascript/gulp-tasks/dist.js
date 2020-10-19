'use strict'
var gulp = require('gulp');
var destfolder = '../webapp';

gulp.task('dist', function () {
    return gulp.src(['./index.html', 'assets', 'main'])
        .pipe(gulp.dest(destfolder));
});

