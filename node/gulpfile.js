const gulp = require('gulp');
const sourcemaps = require('gulp-sourcemaps');
const ts = require('gulp-typescript');
const JSON_FILES = ['src/*.json', 'src/**/*.json'];

// pull in the project TypeScript config
const tsProject = ts.createProject('tsconfig.json');

gulp.task('scripts', function () {
    var tsResult = tsProject.src()
        .pipe(sourcemaps.init()) // This means sourcemaps will be generated
        .pipe(ts({ target: "ES6" }));

    return tsResult.js
        .pipe(sourcemaps.write('.', { includeContent: false, sourceRoot: '../src' }))// Now the sourcemaps are added to the .js file
        .pipe(gulp.dest('dist'));
});

gulp.task('watch', ['scripts', 'copy'], () => {
    gulp.watch('src/**/*.ts', ['scripts']);
});

gulp.task('assets', function () {
    return gulp.src(JSON_FILES)
        .pipe(gulp.dest('dist'));
});

gulp.task('copy', function () {
    gulp.src('public/swagger-ui/dist/*.*')
        .pipe(gulp.dest('dist/public'));
    gulp.src('public/*.json')
        .pipe(gulp.dest('dist/public'));
});

gulp.task('default', ['watch', 'assets']);