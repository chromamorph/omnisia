//COSIATEC � Copyright by David Meredith, 2002.

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <math.h>
#include <time.h>
#include <assert.h>

#define READ 0
#define WRITE 1
#define TRUE 1
#define FALSE 0
#define DOWN 1
#define RIGHT 0
#define END_OF_LINE '\n'
#define END_OF_FILE EOF
#define INTEGER 0
#define REAL 1

//Global types

struct NUMBER_NODE {
	double number;
	struct NUMBER_NODE *next;
};

struct VECTOR_NODE {
	struct VECTOR_NODE *right;
	struct VECTOR_NODE *down;
	struct NUMBER_NODE *vector;
};

struct COV_NODE{
	struct VECTOR_NODE *datapoint;
	struct COV_NODE *next;
};

struct TEC_NODE {
	struct VECTOR_NODE *pattern;
	struct VECTOR_NODE *translator_set;
	int pattern_size;
	int translator_set_size;
	struct COV_NODE *covered_set;
	int coverage;
	double compression_ratio;
	double compactness;
};

struct X_NODE {
	int size;
	struct VECTOR_NODE *vec_seq;
	struct VECTOR_NODE *start_vec;
	struct X_NODE *down;
	struct X_NODE *right;
};

struct BB_NODE {
	struct NUMBER_NODE *max;
	struct NUMBER_NODE *min;
};
		
//Global variables

int REAL_OR_INT;

////////////   FUNCTION DECLARATIONS

int IS_DIGIT(int c);
int READ_CHAR(FILE *F);
void UNREAD_CHAR(int c, FILE *F);
void FREE(void *p);
struct NUMBER_NODE *MAKE_NEW_NUMBER_NODE(void);
struct VECTOR_NODE *MAKE_NEW_VECTOR_NODE(void);
struct COV_NODE *MAKE_NEW_COV_NODE(void);
struct TEC_NODE *MAKE_NEW_TEC_NODE(void);
struct X_NODE *MAKE_NEW_X_NODE(void);
int STRING_LENGTH(char *S);
void PRINT_NUMBER(double n, FILE *F);
void PRINT_CHAR(int c, FILE *F);
char *get_sd_from_command_line(int ac, char *av[]);
void print_help_screen(void);
FILE *OPEN_FILE(char *FN, int DIRECTION);
void PRINT_ERROR_MESSAGE(char *message);
void CLOSE_FILE(FILE *F);
void DELETE_FILE(char *FN);
void PRINT_NEW_LINE(FILE *F);
void COSIATEC(char *DFN, char *OFN, char *SD, double MOE, int COMP_PREF);
struct VECTOR_NODE *READ_VECTOR_SET(FILE *F, int DIR, char *SD);
struct VECTOR_NODE *SORT_DATASET(struct VECTOR_NODE *D);
struct VECTOR_NODE *SETIFY_DATASET(struct VECTOR_NODE *D, double MOE);
void SIATEC(struct VECTOR_NODE *D, char *TFN, double MOE);
int AT_END_OF_LINE(FILE *F);
struct TEC_NODE *READ_TEC(FILE *F, struct VECTOR_NODE *D, double MOE, int COMP_PREF, struct VECTOR_NODE *D2, int SIZE_OF_DATASET);
int IS_BETTER_TEC(struct TEC_NODE *T1, struct TEC_NODE *T2, int COMP_PREF);
struct TEC_NODE *DISPOSE_OF_TEC(struct TEC_NODE *T);
void PRINT_TEC(struct TEC_NODE *T, FILE *F);
struct VECTOR_NODE *DELETE_TEC_COVERED_SET(struct VECTOR_NODE *D, struct TEC_NODE *T);
struct NUMBER_NODE *READ_VECTOR(FILE *F);
struct NUMBER_NODE *SELECT_DIMENSIONS_IN_VECTOR(struct NUMBER_NODE *v, char *SD);
struct VECTOR_NODE *MERGE_DATASET_ROWS(struct VECTOR_NODE *A, struct VECTOR_NODE *B);
int VECTOR_EQUAL(struct NUMBER_NODE *v1, struct NUMBER_NODE *v2, double MOE);
struct VECTOR_NODE *DISPOSE_OF_VECTOR_NODE(struct VECTOR_NODE *v);
void PRINT_SINGLE_POINT_TEC(struct VECTOR_NODE *D, char *TFN);
struct VECTOR_NODE *COMPUTE_VECTORS(struct VECTOR_NODE *D);
struct VECTOR_NODE *CONSTRUCT_VECTOR_TABLE(struct VECTOR_NODE *V);
struct VECTOR_NODE *SORT_VECTORS(struct VECTOR_NODE *V);
struct X_NODE *VECTORIZE_PATTERNS(struct VECTOR_NODE *V, double MOE);
struct X_NODE *SORT_PATTERN_VECTOR_SEQUENCES(struct X_NODE *X);
void PRINT_TECS(struct X_NODE *X, char *FN, double MOE);
void DISPOSE_OF_SIATEC_DATA_STRUCTURES(struct VECTOR_NODE *D, struct VECTOR_NODE *V, struct X_NODE *X);
void SET_TEC_PATTERN_SIZE(struct TEC_NODE *T);
void SET_TEC_TRANSLATOR_SET_SIZE(struct TEC_NODE *T);
void SET_TEC_COVERED_SET(struct TEC_NODE *T, struct VECTOR_NODE *D, double MOE);
void SET_TEC_COVERAGE(struct TEC_NODE *T);
void SET_TEC_COMPRESSION_RATIO(struct TEC_NODE *T);
void PRINT_VECTOR_SET(struct VECTOR_NODE *V, int DIRECTION, FILE *F);
double READ_NUMBER(FILE *F);
int VECTOR_LESS_THAN(struct NUMBER_NODE *v1, struct NUMBER_NODE *v2);
struct NUMBER_NODE *DISPOSE_OF_NUMBER_NODE(struct NUMBER_NODE *v);
struct NUMBER_NODE *VECTOR_MINUS(struct NUMBER_NODE *v1, struct NUMBER_NODE *v2);
struct VECTOR_NODE *MERGE_VECTOR_COLUMNS(struct VECTOR_NODE *A, struct VECTOR_NODE *B);
struct X_NODE *MERGE_PATTERN_ROWS(struct X_NODE *A, struct X_NODE *B);
void PRINT_PATTERN(struct VECTOR_NODE *I, FILE *F);
void PRINT_SET_OF_TRANSLATORS(struct VECTOR_NODE *I, FILE *F);
int PATTERN_VEC_SEQ_EQUAL(struct X_NODE *x1, struct X_NODE *x2, double MOE);
struct X_NODE *DISPOSE_OF_X_NODE(struct X_NODE *x);
struct NUMBER_NODE *VECTOR_PLUS(struct NUMBER_NODE *v1, struct NUMBER_NODE *v2);
void PRINT_VECTOR(struct NUMBER_NODE *v, FILE *F);
int PATTERN_VEC_SEQ_LESS_THAN(struct X_NODE *x1, struct X_NODE *x2);
int VEC_LIST_EQUAL(struct VECTOR_NODE *v1, struct VECTOR_NODE *v2, int DIRECTION, double MOE);
int VEC_LIST_LESS_THAN(struct VECTOR_NODE *v1, struct VECTOR_NODE *v2, int DIRECTION);
double get_margin_of_error_from_command_line(int ac, char *av[]);
int get_comp_pref_from_command_line(int ac, char *av[]);
void SET_TEC_COMPACTNESS(struct TEC_NODE *T, struct VECTOR_NODE *D2, double MOE, int SIZE_OF_DATASET);
struct VECTOR_NODE *TRANSLATE_PATTERN(struct VECTOR_NODE *P1, struct NUMBER_NODE *v, int DIRECTION);
struct BB_NODE *COMPUTE_BB_OF_PATTERN(struct VECTOR_NODE *P);
int COMPUTE_NUM_POINTS_IN_BB(struct BB_NODE *BB, struct VECTOR_NODE *D2, double MOE);
struct BB_NODE *DISPOSE_OF_BB_NODE(struct BB_NODE *BB);
struct VECTOR_NODE *DISPOSE_OF_PATTERN(struct VECTOR_NODE *P, int DIRECTION);
struct BB_NODE *MAKE_NEW_BB_NODE(void);
int COMPUTE_SIZE_OF_DATASET(struct VECTOR_NODE *D2);
int IN_BB(struct NUMBER_NODE *v, struct BB_NODE *BB, double MOE);
int COMPUTE_SIZE_OF_DATASET(struct VECTOR_NODE *D2);

////////////    FUNCTION DEFINITIONS

int main(int argc, char *argv[]) {
	char *SD, FN[100], OFN[100];
	double MOE;
	int i;
	int COMP_PREF;
	
	SD = NULL;
	if (argc == 1)
		print_help_screen();
	else {
		REAL_OR_INT = INTEGER;
		SD = get_sd_from_command_line(argc, argv);
		MOE = get_margin_of_error_from_command_line(argc, argv);
		COMP_PREF = get_comp_pref_from_command_line(argc,argv);
		strcpy(FN,argv[argc-1]);
		strcpy(OFN,argv[argc-1]);
		for (i = 2; argc-i > 0; i++)
			strcat(OFN,argv[argc-i]);
		strcat(OFN,".cos");
		COSIATEC(FN,OFN,SD,MOE,COMP_PREF);
		};
	return 0;
	}

int IS_DIGIT(int c) {
	if (isdigit(c))
		return TRUE;
	else {
		return FALSE;
	}
	}

int READ_CHAR(FILE *F) {
	return fgetc(F);
	}
	
void UNREAD_CHAR(int c, FILE *F) {
	ungetc(c,F);
	}
	
void FREE(void *p) {
	free(p);
	}
	
struct NUMBER_NODE *MAKE_NEW_NUMBER_NODE(void) {
	struct NUMBER_NODE *n;
	
	n = (struct NUMBER_NODE *)malloc(sizeof(struct NUMBER_NODE));
	assert(n != NULL);
	if (n == NULL) {
		PRINT_ERROR_MESSAGE("MAKE_NEW_NUMBER_NODE: malloc returned NULL.");
		exit(1);
		};
	n->number = 0;
	n->next = NULL;
	return n;
	}
	
struct VECTOR_NODE *MAKE_NEW_VECTOR_NODE(void) {
	struct VECTOR_NODE *v;
	
	v = (struct VECTOR_NODE *)malloc(sizeof(struct VECTOR_NODE));
	assert(v != NULL);
	if (v == NULL) {
		PRINT_ERROR_MESSAGE("MAKE_NEW_VECTOR_NODE: malloc returned NULL.");
		exit(1);
		};
	v->right = v->down = NULL;
	v->vector = NULL;
	return v;
	}

struct COV_NODE *MAKE_NEW_COV_NODE(void) {
	struct COV_NODE *c;
	
	c = (struct COV_NODE *)malloc(sizeof(struct COV_NODE));
	assert(c != NULL);
	if (c == NULL) {
		PRINT_ERROR_MESSAGE("MAKE_NEW_COV_NODE: malloc returned NULL.");
		exit(1);
		};
	c->datapoint = NULL;
	c->next = NULL;
	return c;
	}
	
struct TEC_NODE *MAKE_NEW_TEC_NODE(void) {
	struct TEC_NODE *t;
	
	t = (struct TEC_NODE *)malloc(sizeof(struct TEC_NODE));
	assert(t != NULL);
	if (t == NULL) {
		PRINT_ERROR_MESSAGE("MAKE_NEW_TEC_NODE: malloc returned NULL.");
		exit(1);
		};
	t->pattern = NULL;
	t->translator_set = NULL;
	t->pattern_size = 0;
	t->translator_set_size = 0;
	t->covered_set = NULL;
	t->coverage = 0;
	t->compression_ratio = 0;
	t->compactness = 0;
	return t;
	}
	
struct X_NODE *MAKE_NEW_X_NODE(void) {
	struct X_NODE *x;
	
	x = (struct X_NODE *)malloc(sizeof(struct X_NODE));
	assert(x != NULL);
	if (x == NULL) {
		PRINT_ERROR_MESSAGE("MAKE_NEW_X_NODE: malloc returned NULL.");
		exit(1);
		};
	x->size = 0;
	x->vec_seq = x->start_vec = NULL;
	x->down = x->right = NULL;
	return x;
	}
	
int STRING_LENGTH(char *S) {
	return strlen(S);
	}
	
void PRINT_NUMBER(double n, FILE *F) {
	if (REAL_OR_INT == REAL)
		fprintf(F,"%f", n);
	else if (REAL_OR_INT == INTEGER)
		fprintf(F,"%d", ((int) floor(n)));
	}
	
void PRINT_CHAR(int c, FILE *F) {
	fputc(c,F);
	}
	
double get_margin_of_error_from_command_line(int ac, char *av[]) {
	int i;
	
	for (i = 1; i < ac - 1; i++)
		if (strncmp(av[i],"-m",2) == 0)
			return atof(av[i]+2);
	return 0;
	}

int get_comp_pref_from_command_line(int ac, char *av[]) {
	int i;
	
	for (i = 1; i < ac - 1; i++)
		if (strncmp(av[i],"-c",2) == 0)
			return TRUE;
	return FALSE;
	}

char *get_sd_from_command_line(int ac, char *av[]) {
	int i;
	
	for (i = 1; i < ac - 1; i++)
		if (strncmp(av[i],"-d",2) == 0)
			return (av[i]+2);
	return NULL;
	}
	
void print_help_screen(void) {
	printf("\n\nCOSIATEC help\n=============\n\n");
	printf("  SYNTAX\n  ======\n\n");
	printf("    cosiatec [OPTIONS] file-name\n\n");
	printf("  OPTIONS\n  =======\n\n");
	printf("    -dxxx      xxx is a string of 0s and 1s\n");
	printf("               representing the chosen dimensions.\n\n");
	printf("    -m         Margin of error (typically .0001 or 0).\n\n");
	printf("    -c         Choose TECs according to following order:\n");
	printf("                 Maximal bounding-box compactness.\n");
	printf("                 Compression ratio.\n");
	printf("                 Number of datapoints covered.\n");
	printf("                 Pattern size.");
	printf("\n");
	}

FILE *OPEN_FILE(char *FN, int DIRECTION) {
	char *mode;
	
	if (DIRECTION == READ)
		mode = "r";
	if (DIRECTION == WRITE)
		mode = "w";
	return fopen(FN,mode);
	}

void PRINT_ERROR_MESSAGE(char *message) {
	printf("\n\nERROR!\n======\n\n");
	printf("  %s\n\n",message);
	}

void CLOSE_FILE(FILE *F) {
	fclose(F);
	}

void DELETE_FILE(char *FN) {
	remove(FN);
	}

void PRINT_NEW_LINE(FILE *F) {
	fprintf(F,"\n");
	}

void COSIATEC(char *DFN, char *OFN, char *SD, double MOE, int COMP_PREF) {
	char *TFN;
	FILE *OF, *TF, *DF;
	struct VECTOR_NODE *D, *D2;
	struct TEC_NODE *BT, *T;
	int SIZE_OF_DATASET;
	
	SIZE_OF_DATASET = 0;
	D2 = NULL;
	TFN = "TEMP_TEC_FILE";
	if ((DF = OPEN_FILE(DFN, READ)) == NULL) {
		PRINT_ERROR_MESSAGE("FILE DOES NOT EXIST.");
		exit(1); 
		};
	D = READ_VECTOR_SET(DF,DOWN,SD);
	CLOSE_FILE(DF);
	D = SORT_DATASET(D);
	D = SETIFY_DATASET(D,MOE);
	if (COMP_PREF == TRUE) {
		DF = OPEN_FILE(DFN,READ);
		D2 = READ_VECTOR_SET(DF,DOWN,SD);
		CLOSE_FILE(DF);
		D2 = SORT_DATASET(D);
		D2 = SETIFY_DATASET(D,MOE);
		SIZE_OF_DATASET = COMPUTE_SIZE_OF_DATASET(D2);
		};
	OF = OPEN_FILE(OFN,WRITE);
	BT = NULL;
	T = NULL;
	while (D != NULL) {
		SIATEC(D,TFN,MOE);
printf("Just done SIATEC.\n");
fflush(stdout);
		TF = OPEN_FILE(TFN,READ);
		while (!AT_END_OF_LINE(TF)) {
			T = READ_TEC(TF,D,MOE,COMP_PREF,D2,SIZE_OF_DATASET);
printf("Just done READ_TEC.\n");
fflush(stdout);
			if (IS_BETTER_TEC(T,BT,COMP_PREF)){
printf("Just done IS_BETTER_TEC.\n");
fflush(stdout);
				BT = DISPOSE_OF_TEC(BT);
				BT = T;
				T = NULL;
				}
			else
				T = DISPOSE_OF_TEC(T);
			};
printf("Completed inner while loop.\n");
fflush(stdout);
		CLOSE_FILE(TF);
		DELETE_FILE(TFN);
		PRINT_TEC(BT,OF);
		D = DELETE_TEC_COVERED_SET(D,BT);
		BT = DISPOSE_OF_TEC(BT);
printf("Completed outer while loop.\n");
fflush(stdout);
		};
	PRINT_NEW_LINE(OF);
	CLOSE_FILE(OF);
	}
	
int COMPUTE_SIZE_OF_DATASET(struct VECTOR_NODE *D2) {
	//Assumes dataset is stored as right-directed, sorted, setified list.
	int n;
	struct VECTOR_NODE *d;
	
	d = D2;
	n = 0;
	while (d != NULL) {
		d = d->right;
		n++;
		};
	return n;
	};
	
struct VECTOR_NODE *READ_VECTOR_SET(FILE *F, int DIR, char *SD) {
	struct VECTOR_NODE *S, *L;
	struct NUMBER_NODE *v;
	
	S = L = NULL;
	v = NULL;
	while (!AT_END_OF_LINE(F)) {
		v = READ_VECTOR(F);
		if (SD != NULL)
			v = SELECT_DIMENSIONS_IN_VECTOR(v,SD);
		if (S == NULL) {
			S = MAKE_NEW_VECTOR_NODE();
			S->vector = v;
			v = NULL;
			L = S;
			}
		else if (DIR == DOWN) {
			L->down = MAKE_NEW_VECTOR_NODE();
			L = L->down;
			L->vector = v;
			v = NULL;
			}
		else {
			L->right = MAKE_NEW_VECTOR_NODE();
			L = L->right;
			L->vector = v;
			v = NULL;
			};
		};
	return S;
	}
	
struct VECTOR_NODE *SORT_DATASET(struct VECTOR_NODE *D) {
	struct VECTOR_NODE *ABOVE_A, *A, *B, *BELOW_B, *C;
	
	while (D != NULL && D->down != NULL) {
		ABOVE_A = NULL;
		A = D;
		D = NULL;
		do {
			if (D != NULL)
				ABOVE_A->down = NULL;
			B = A->down;
			A->down = NULL;
			BELOW_B = B->down;
			B->down = NULL;
			C = MERGE_DATASET_ROWS(A,B);
			if (D == NULL)
				D = C;
			else
				ABOVE_A->down = C;
			C->down = BELOW_B;
			ABOVE_A = C;
			A = ABOVE_A->down;
		} while (A != NULL && A->down != NULL);
		};
	return D;
	}
	
struct VECTOR_NODE *SETIFY_DATASET(struct VECTOR_NODE *D, double MOE) {
	struct VECTOR_NODE *d1, *d2;
	
	d1 = D;
	d2 = NULL;
	while (d1 != NULL && d1->right != NULL) {
		if (VECTOR_EQUAL(d1->right->vector,d1->vector,MOE)) {
			d2 = d1->right;
			d1->right = d2->right;
			d2->right = NULL;
			d2 = DISPOSE_OF_VECTOR_NODE(d2);
			}
		else
			d1 = d1->right;
		};
	return D;
	}

void SIATEC(struct VECTOR_NODE *D, char *TFN, double MOE) {
	struct VECTOR_NODE *V;
	struct X_NODE *X;

	if (D->right == NULL)
		PRINT_SINGLE_POINT_TEC(D,TFN);
	else {
		V = COMPUTE_VECTORS(D);
//printf("Just done COMPUTE_VECTORS.\n");
//fflush(stdout);
		V = CONSTRUCT_VECTOR_TABLE(V);
//printf("Just done CONSTRUCT_VECTOR_TABLE.\n");
//fflush(stdout);
		V = SORT_VECTORS(V);
//printf("Just done SORT_VECTORS.\n");
//fflush(stdout);
		X = VECTORIZE_PATTERNS(V,MOE);
//printf("Just done VECTORIZE_PATTERNS.\n");
//fflush(stdout);
		X = SORT_PATTERN_VECTOR_SEQUENCES(X);
//printf("Just done SORT_PATTERN_VECTOR_SEQUENCES.\n");
//fflush(stdout);
		PRINT_TECS(X,TFN,MOE);
//printf("Just done PRINT_TECS.\n");
//fflush(stdout);
		DISPOSE_OF_SIATEC_DATA_STRUCTURES(D,V,X);
//printf("Just done DISPOSE_OF_SIATEC_DATA_STRUCTURES.\n");
//fflush(stdout);
		}
	}
	
int AT_END_OF_LINE(FILE *F) {
	int c;

	c = READ_CHAR(F);
	if (c == END_OF_LINE || c == END_OF_FILE)
		return TRUE;
	else {
		UNREAD_CHAR(c,F);
		return FALSE;
		}
	}
	
struct TEC_NODE *READ_TEC(FILE *F, struct VECTOR_NODE *D, double MOE, int COMP_PREF, struct VECTOR_NODE *D2, int SIZE_OF_DATASET) {
	struct TEC_NODE *T;

	T = MAKE_NEW_TEC_NODE();
	T->pattern = READ_VECTOR_SET(F,DOWN,NULL);
	T->translator_set = READ_VECTOR_SET(F,DOWN,NULL);
	SET_TEC_PATTERN_SIZE(T);
	SET_TEC_TRANSLATOR_SET_SIZE(T);
	SET_TEC_COVERED_SET(T,D,MOE);
	SET_TEC_COVERAGE(T);
	SET_TEC_COMPRESSION_RATIO(T);
	if (COMP_PREF == TRUE) {
		SET_TEC_COMPACTNESS(T,D2,MOE,SIZE_OF_DATASET);
		};
	return T;
	}

void SET_TEC_COMPACTNESS(struct TEC_NODE *T, struct VECTOR_NODE *D2, double MOE, int SIZE_OF_DATASET) {
	/*
	This function finds the bounding-box compactness of each pattern in the TEC.
	It records the bounding-box compactness of the most compact pattern in the TEC
	in the field T->compactness.
	
	The bounding-box compactness of a pattern is the ratio of the number of datapoints
	in the pattern to the number of datapoints in the dataset that are within or on the 
	boundaries of the bounding-box of the pattern.
	
	The bounding box compactness of a pattern ranges from 0 for an empty pattern to 1 for
	a pattern that contains all the datapoints in the dataset within its bounding box.
	
	The bounding box compactness of an empty pattern is defined to be 0. Any non-empty
	pattern contains at least one datapoint and therefore has non-zero compactness.
	*/
	struct BB_NODE *BB;
	int MIN_NUM_POINTS_IN_BB, NUM_POINTS_IN_BB;
	struct VECTOR_NODE *t, *P;
	
	if (T->pattern_size == 0)
		T->compactness = 0;
	else {
		MIN_NUM_POINTS_IN_BB = SIZE_OF_DATASET;
		t = T->translator_set;
		while (t != NULL) {
			P = TRANSLATE_PATTERN(T->pattern,t->vector,DOWN);
printf("Just translated pattern.\n");
fflush(stdout);
assert(P != NULL);
			BB = COMPUTE_BB_OF_PATTERN(P);
printf("Just computed BB.\n");
fflush(stdout);
assert(BB != NULL);
			NUM_POINTS_IN_BB = COMPUTE_NUM_POINTS_IN_BB(BB,D2,MOE);
printf("Just computed number of points in BB = %d.\n",NUM_POINTS_IN_BB);
fflush(stdout);
			if (NUM_POINTS_IN_BB < MIN_NUM_POINTS_IN_BB)
				MIN_NUM_POINTS_IN_BB = NUM_POINTS_IN_BB;
			t = t->down;
			BB = DISPOSE_OF_BB_NODE(BB);
printf("Just disposed of BB.\n");
fflush(stdout);
assert(BB == NULL);
			P = DISPOSE_OF_PATTERN(P,DOWN);
printf("Just disposed of P.\n");
fflush(stdout);
assert(P == NULL);
			};
		T->compactness = ((double)(T->pattern_size))/((double)(MIN_NUM_POINTS_IN_BB));
printf("T->compactness = %f.\n",T->compactness);
fflush(stdout);
assert(MIN_NUM_POINTS_IN_BB > 0);
		};
	}

struct VECTOR_NODE *DISPOSE_OF_PATTERN(struct VECTOR_NODE *P, int DIRECTION) {
	//Assumes that either down or right field of each NODE is NULL.
	struct VECTOR_NODE *p;
	struct NUMBER_NODE *n;
	
	while (P != NULL) {
		if (DIRECTION == DOWN) {
			assert(P->right == NULL);
			p = P->down;
			P->down = NULL;
			}
		else {
			assert(P->down == NULL);
			p = p->right;
			P->right = NULL;
			};
		while (P->vector != NULL) {
			n = P->vector->next;
			P->vector->next = NULL;
			free(P->vector);
			P->vector = n;
			};
		free(P);
		P = p;
		};
	return NULL;
	}

struct BB_NODE *DISPOSE_OF_BB_NODE(struct BB_NODE *BB) {
	struct NUMBER_NODE *n;
	
	while (BB->min != NULL) {
		n = BB->min->next;
		BB->min->next = NULL;
		free(BB->min);
		BB->min = n;
		};
	if (BB->max != NULL) {
		n = BB->max->next;
		BB->max->next = NULL;
		free(BB->max);
		BB->max = n;
		};
	free(BB);
	return NULL;
	}

int COMPUTE_NUM_POINTS_IN_BB(struct BB_NODE *BB, struct VECTOR_NODE *D2, double MOE) {
	//Assumes D2 is head of right-directed, sorted, setified dataset list.
	int n;
	struct VECTOR_NODE *d;
	
	for (n = 0, d = D2; d != NULL; d = d->right)
		if (IN_BB(d->vector,BB,MOE))
			n++;
	return n;
	}

int IN_BB(struct NUMBER_NODE *v, struct BB_NODE *BB, double MOE) {
	struct NUMBER_NODE *n, *nmin, *nmax;
	
	for (n = v, nmin = BB->min, nmax = BB->max;
		 n != NULL;
		 n = n->next, nmin = nmin->next, nmax = nmax->next)
		if (n->number - nmin->number < -MOE || n->number - nmax->number > MOE)
			return FALSE;
	return TRUE;
	}

struct BB_NODE *COMPUTE_BB_OF_PATTERN(struct VECTOR_NODE *P) {
	//Assumes pattern is a down-directed list of VECTOR_NODEs.
	struct BB_NODE *BB;
	struct NUMBER_NODE *n, *nmin, *nmax;
	struct VECTOR_NODE *p;
	
	if (P == NULL) {
		PRINT_ERROR_MESSAGE("COMPUTE_BB_OF_PATTERN called with NULL pattern P.");
		exit(1);
		}
	//Initialise bounding box.
	BB = NULL;
	BB = MAKE_NEW_BB_NODE();
	for (n = P->vector; n != NULL; n = n->next) {
		if (BB->min == NULL) {
			BB->min = MAKE_NEW_NUMBER_NODE();
			nmin = BB->min;
			}
		else {
			nmin->next = MAKE_NEW_NUMBER_NODE();
			nmin = nmin->next;
			};
		if (BB->max == NULL) {
			BB->max = MAKE_NEW_NUMBER_NODE();
			nmax = BB->max;
			}
		else {
			nmax->next = MAKE_NEW_NUMBER_NODE();
			nmax = nmax->next;
			};
		nmin->number = nmax->number = n->number;
		};
	//Compute bounding box.
	for (p = P; p != NULL; p = p->down) {
		for (n = p->vector, nmin = BB->min, nmax = BB->max; 
			 n != NULL; 
			 n = n->next, nmin = nmin->next, nmax = nmax->next) {
			if (n->number < nmin->number)
				nmin->number = n->number;
			if (n->number > nmax->number)
				nmax->number = n->number;
			}
		}
	return BB;
	}

struct BB_NODE *MAKE_NEW_BB_NODE(void) {
	struct BB_NODE *BB;
	
	BB = (struct BB_NODE *)malloc(sizeof(struct BB_NODE));
	assert(BB != NULL);
	if (BB == NULL) {
		PRINT_ERROR_MESSAGE("MAKE_NEW_BB_NODE: malloc returned NULL.");
		exit(1);
		};
	BB->min = NULL;
	BB->max = NULL;
	return BB;
	}

struct VECTOR_NODE *TRANSLATE_PATTERN(struct VECTOR_NODE *P1, struct NUMBER_NODE *v, int DIRECTION) {
	struct VECTOR_NODE *p1, *p2, *P2;
	
	if (v == NULL)
		return P1;
	if (P1 == NULL) {
		PRINT_ERROR_MESSAGE("TRANSLATE_PATTERN called with NULL pattern.");
		exit(1);
		}
	p1 = P1;
	P2 = NULL;
	while (p1 != NULL) {
		if (P2 == NULL) {
			P2 = MAKE_NEW_VECTOR_NODE();
			p2 = P2;
			}
		else if (DIRECTION == DOWN) {
			p2->down = MAKE_NEW_VECTOR_NODE();
			p2 = p2->down;
			}
		else {
			p2->right = MAKE_NEW_VECTOR_NODE();
			p2 = p2->right;
			};
		p2->vector = VECTOR_PLUS(p1->vector,v);
		if (DIRECTION == DOWN)
			p1 = p1->down;
		else
			p1 = p1->right;
		};
	return P2;
	}

int IS_BETTER_TEC(struct TEC_NODE *T1, struct TEC_NODE *T2, int COMP_PREF) {
	if (T1 == NULL) {
		PRINT_ERROR_MESSAGE("IS_BETTER_TEC: T1 is NULL.");
		exit(1);
		};
	if (T2 == NULL)
		return TRUE;
	if (COMP_PREF == TRUE && T1->compactness > T2->compactness)
		return TRUE;
	if (COMP_PREF == TRUE && T1->compactness < T2->compactness)
		return FALSE;
	if (T1->compression_ratio > T2->compression_ratio)
		return TRUE;
	if (T1->compression_ratio < T2->compression_ratio)
		return FALSE;
	if (T1->coverage > T2->coverage)
		return TRUE;
	if (T1->coverage < T2->coverage)
		return FALSE;
	if (T1->pattern_size > T2->pattern_size)
		return TRUE;
	if (T1->pattern_size < T2->pattern_size)
		return FALSE;
	return FALSE;
	}

struct TEC_NODE *DISPOSE_OF_TEC(struct TEC_NODE *T) {
	struct COV_NODE *c;

	if (T == NULL)
		return NULL;
	T->pattern = DISPOSE_OF_VECTOR_NODE(T->pattern);
	T->translator_set = DISPOSE_OF_VECTOR_NODE(T->translator_set);
	while (T->covered_set != NULL) {
		c = T->covered_set->next;
		T->covered_set->next = NULL;
		T->covered_set->datapoint = NULL;
		FREE(T->covered_set);
		T->covered_set = c;
		};
	FREE(T);
	return NULL;
	}
	
void PRINT_TEC(struct TEC_NODE *T, FILE *F) {
	if (T != NULL) {
		PRINT_VECTOR_SET(T->pattern,DOWN,F);
		PRINT_VECTOR_SET(T->translator_set,DOWN,F);
		}
	}

struct VECTOR_NODE *DELETE_TEC_COVERED_SET(struct VECTOR_NODE *D, struct TEC_NODE *T) {
	struct COV_NODE *c;
	struct VECTOR_NODE *d;

	c = T->covered_set;
	while (c != NULL && D == c->datapoint) {
		D = D->right;
		c->datapoint->right = NULL;
		c->datapoint = DISPOSE_OF_VECTOR_NODE(c->datapoint);
		c = c->next;
		};
	d = D;
	while (c != NULL) {
		while (d->right != c->datapoint)
			d = d->right;
		d->right = c->datapoint->right;
		c->datapoint->right = NULL;
		c->datapoint = DISPOSE_OF_VECTOR_NODE(c->datapoint);
		c = c->next;
		};
	return D;
	}
	
struct NUMBER_NODE *READ_VECTOR(FILE *F) {
	struct NUMBER_NODE *v, *p;

	v = NULL;
	while (!AT_END_OF_LINE(F)) {
		if (v == NULL) {
			v = MAKE_NEW_NUMBER_NODE();
			p = v;
			}
		else {
			p->next = MAKE_NEW_NUMBER_NODE();
			p = p->next;
			};
		p->number = READ_NUMBER(F);
		};
	return v;
	}
	
struct NUMBER_NODE *SELECT_DIMENSIONS_IN_VECTOR(struct NUMBER_NODE *v, char *SD) {
	int n, i;
	struct NUMBER_NODE *p, *q;

	q = NULL;
	n = STRING_LENGTH(SD);
	p = v;
	i = 0;
	while (i < n && SD[i] == '0') {
		v = p->next;
		p->next = NULL;
		p = DISPOSE_OF_NUMBER_NODE(p);
		p = v;
		i = i + 1;
		};
	while (i < n) {
		if (SD[i] == '0') {
			if (q == NULL)
				q = v;
			while (q->next != p)
				q = q->next;
			q->next = p->next;
			p->next = NULL;
			p = DISPOSE_OF_NUMBER_NODE(p);
			p = q->next;
			}
		else
			p = p->next;
		i = i + 1;
		};
	return v;
	}

struct VECTOR_NODE *MERGE_DATASET_ROWS(struct VECTOR_NODE *A, struct VECTOR_NODE *B) {
	struct VECTOR_NODE *a, *b, *C, *c;

	a = A;
	b = B;
	if (VECTOR_LESS_THAN(a->vector,b->vector)) {
		C = a;
		a = a->right;
		}
	else {
		C = b;
		b = b->right;
		};
	C->right = NULL;
	c = C;
	while (a != NULL && b != NULL) {
		if (VECTOR_LESS_THAN(a->vector,b->vector)) {
			c->right = a;
			a = a->right;
			}
		else {
			c->right = b;
			b = b->right;
			};
		c = c->right;
		c->right = NULL;
		};
	if (a == NULL)
		c->right = b;
	else
		c->right = a;
	return C;
	}
	
int VECTOR_EQUAL(struct NUMBER_NODE *v1, struct NUMBER_NODE *v2, double MOE) {
	double d;
	
	if (v1 == NULL && v2 == NULL)
		return TRUE;
	if (v1 == NULL || v2 == NULL)
		return FALSE;
	d = v1->number - v2->number;
	if (d < 0)
		d = -d;
	if (d > MOE)
		return FALSE;
	return (VECTOR_EQUAL(v1->next,v2->next,MOE));
	}
	
struct VECTOR_NODE *DISPOSE_OF_VECTOR_NODE(struct VECTOR_NODE *v) {
	if (v == NULL) {
		return NULL;
		};
	v->vector = DISPOSE_OF_NUMBER_NODE(v->vector);
	v->right = DISPOSE_OF_VECTOR_NODE(v->right);
	v->down = DISPOSE_OF_VECTOR_NODE(v->down);
	FREE(v);
	return NULL;
	}

void PRINT_SINGLE_POINT_TEC(struct VECTOR_NODE *D, char *TFN) {
	FILE *TF;
	struct NUMBER_NODE *ZERO_VECTOR;
	
	TF = OPEN_FILE(TFN,WRITE);
	PRINT_VECTOR(D->vector,TF);
	PRINT_NEW_LINE(TF);
	ZERO_VECTOR = VECTOR_MINUS(D->vector,D->vector);
	PRINT_VECTOR(ZERO_VECTOR,TF);
	PRINT_NEW_LINE(TF);
	PRINT_NEW_LINE(TF);
	CLOSE_FILE(TF);
	}

struct VECTOR_NODE *COMPUTE_VECTORS(struct VECTOR_NODE *D) {
	struct VECTOR_NODE *d1, *d2, *p, *v, *V;

	V = NULL;
	if (D != NULL) {
		d1 = D;
		while (d1 != NULL) {
			p = d1;
			d2 = D;
			while (d2 != NULL) {
				p->down = MAKE_NEW_VECTOR_NODE();
				p = p->down;
				p->right = d1;
				p->vector = VECTOR_MINUS(d2->vector,d1->vector);
				if (d1 == d2 && d1->right != NULL) {
					if (V == NULL) {
						V = MAKE_NEW_VECTOR_NODE();
						v = V;
						}
					else {
						v->right = MAKE_NEW_VECTOR_NODE();
						v = v->right;
						};
					v->down = p;
					};
				d2 = d2->right;
				};
			d1 = d1->right;
			};
		};
	return V;
	}

struct VECTOR_NODE *CONSTRUCT_VECTOR_TABLE(struct VECTOR_NODE *V) {
	struct VECTOR_NODE *p, *v, *w;

	p = V;
	while (p != NULL) {
		v = p->down->down;
		w = p;
		while (v != NULL) {
			w->down = MAKE_NEW_VECTOR_NODE();
			w = w->down;
			w->right = v;
			v = v->down;
			};
		p = p->right;
		};
	return V;
	}

struct VECTOR_NODE *SORT_VECTORS(struct VECTOR_NODE *V) {
	struct VECTOR_NODE *BEFORE_A, *A, *B, *AFTER_B, *C;

	while (V != NULL && V->right != NULL) {
		BEFORE_A = NULL;
		A = V;
		V = NULL;
		do {
			if (V != NULL)
				BEFORE_A->right = NULL;
			B = A->right;
			A->right = NULL;
			AFTER_B = B->right;
			B->right = NULL;
			C = MERGE_VECTOR_COLUMNS(A,B);
			if (B != NULL) {
				B->down = NULL;
				B = DISPOSE_OF_VECTOR_NODE(B);
				};
			if (V == NULL)
				V = C;
			else
				BEFORE_A->right = C;
			C->right = AFTER_B;
			BEFORE_A = C;
			A = BEFORE_A->right;
		} while (A != NULL && A->right != NULL);
		};
	BEFORE_A = A = B = AFTER_B = C = NULL;
	if (V != NULL) {
		A = V->down;
		V->down = NULL;
		DISPOSE_OF_VECTOR_NODE(V);
		V = A;
		A = NULL;
		};
	return V;
	}

struct X_NODE *VECTORIZE_PATTERNS(struct VECTOR_NODE *V, double MOE) {
	struct VECTOR_NODE *i, *j, *above_j, *Q, *q;
	struct X_NODE *x, *X;
	int size;

	i = j = above_j = Q = q = NULL;
	x = X = NULL;
	i = V;
	while (i != NULL) {
		size = 0;
		j = i->down;
		above_j = i;
		while (j != NULL && VECTOR_EQUAL(i->right->vector,j->right->vector,MOE)) {
			if (Q == NULL) {
				Q = MAKE_NEW_VECTOR_NODE();
				q = Q;
				}
			else {
				q->down = MAKE_NEW_VECTOR_NODE();
				q = q->down;
				};
			size = size + 1;
			q->vector = VECTOR_MINUS(j->right->right->vector,above_j->right->right->vector);
			j = j->down;
			above_j = above_j->down;
			};
		if (X == NULL) {
			X = MAKE_NEW_X_NODE();
			x = X;
			}
		else {
			x->down = MAKE_NEW_X_NODE();
			x = x->down;
			};
		x->size = size;
		x->vec_seq = Q;
		x->start_vec = i;
		Q = q = NULL;
		i = j;
		};
	i = j = above_j = Q = q = NULL;
	x = NULL;
	return X;
	}

struct X_NODE *SORT_PATTERN_VECTOR_SEQUENCES(struct X_NODE *X) {
	struct X_NODE *ABOVE_A, *A, *B, *BELOW_B, *C;

	while (X != NULL && X->down != NULL) {
		ABOVE_A = NULL;
		A = X;
		X = NULL;
		do {
			if (X != NULL)
				ABOVE_A->down = NULL;
			B = A->down;
			A->down = NULL;
			BELOW_B = B->down;
			B->down = NULL;
			C = MERGE_PATTERN_ROWS(A,B);
			if (X == NULL)
				X = C;
			else
				ABOVE_A->down = C;
			C->down = BELOW_B;
			ABOVE_A = C;
			A = ABOVE_A->down;
		} while (A != NULL && A->down != NULL);
		};
	ABOVE_A = A = B = BELOW_B = C = NULL;
	return X;
	}

void PRINT_TECS(struct X_NODE *X, char *FN, double MOE) {
	struct X_NODE *i, *before_i;
	struct VECTOR_NODE *Iptr, *j, *I;
	FILE *F;

	F = OPEN_FILE(FN,WRITE);
	i = before_i = NULL;
	I = Iptr = j = NULL;
	i = X;
	if (X != NULL) {
		do {
			j = i->start_vec;
			if (I != NULL) {
				Iptr = I;
				while (Iptr != NULL) {
					Iptr->right = NULL;
					Iptr = Iptr->down;
					};
				I = DISPOSE_OF_VECTOR_NODE(I);
				};
			while (j != NULL && VECTOR_EQUAL(j->right->vector,i->start_vec->right->vector,MOE)) {
				if (I == NULL) {
					I = MAKE_NEW_VECTOR_NODE();
					Iptr = I;
					}
				else {
					Iptr->down = MAKE_NEW_VECTOR_NODE();
					Iptr = Iptr->down;
					};
				Iptr->right = j->right->right;
				j = j->down;
				};
			PRINT_PATTERN(I,F);
			PRINT_SET_OF_TRANSLATORS(I,F);
			before_i = i;
			i = i->right;
			while (i != NULL && PATTERN_VEC_SEQ_EQUAL(i,before_i,MOE)) {
				i = i->right;
				before_i = before_i->right;
				};
		} while (i != NULL);
		if (I != NULL) {
			Iptr = I;
			while (Iptr != NULL) {
				Iptr->right = NULL;
				Iptr = Iptr->down;
				};
			I = DISPOSE_OF_VECTOR_NODE(I);
			};
		j = NULL;
		i = before_i = NULL;
		};
	PRINT_NEW_LINE(F);
	CLOSE_FILE(F);
	}

void DISPOSE_OF_SIATEC_DATA_STRUCTURES(struct VECTOR_NODE *D, struct VECTOR_NODE *V, struct X_NODE *X) {
	struct VECTOR_NODE *p1, *p2;
	struct X_NODE *x;

	p1 = D;
	while (p1 != NULL) {
		p2 = p1->down;
		while (p2 != NULL) {
			p2->right = NULL;
			p2 = p2->down;
			};
		p1 = p1->right;
		};
//printf("Just done first part of DISPOSE_OF_SIATEC_DATA_STRUCTURES.\n");
//fflush(stdout);
	p1 = V;
	while (p1 != NULL) {
		p1->right = NULL;
		if (p1->vector != NULL) PRINT_VECTOR(p1->vector, stdout);
		p1 = p1->down;
		};
//printf("Just done second part of DISPOSE_OF_SIATEC_DATA_STRUCTURES.\n");
//fflush(stdout);
	x = X;
	while (x != NULL) {
		x->start_vec = NULL;
		assert(x->down == NULL);
		while (x->vec_seq != NULL) {
			p1 = x->vec_seq->down;
			assert(x->vec_seq->right == NULL);
			x->vec_seq->down = NULL;
			free(x->vec_seq);
			x->vec_seq = p1;
			};
		x = x->right;
		};
//printf("Just done third part of DISPOSE_OF_SIATEC_DATA_STRUCTURES.\n");
//fflush(stdout);
	p1 = D;
	while (p1 != NULL) {
		p1->down = DISPOSE_OF_VECTOR_NODE(p1->down);
		p1 = p1->right;
		};
//printf("Just done fourth part of DISPOSE_OF_SIATEC_DATA_STRUCTURES.\n");
//fflush(stdout);
	while (V != NULL) {
		p1 = V->down;
		V->down = NULL;
		assert(V->right == NULL);
		assert(V->vector == NULL);
		free(V);
		V = p1;
		};
//printf("Just disposed of V.\n");
//fflush(stdout);
	while (X != NULL) {
		x = X->right;
		X->right = NULL;
		assert(X->vec_seq == NULL);
		assert(X->start_vec == NULL);
		assert(X->down == NULL);
		free(X);
		X = x;
		};
//printf("Just disposed of X.\n");
//fflush(stdout);
	}
	
void SET_TEC_PATTERN_SIZE(struct TEC_NODE *T) {
	struct VECTOR_NODE *v;

	v = T->pattern;
	T->pattern_size = 0;
	while (v != NULL) {
		v = v->down;
		T->pattern_size = T->pattern_size + 1;
		};
	}
	
void SET_TEC_TRANSLATOR_SET_SIZE(struct TEC_NODE *T) {
	struct VECTOR_NODE *v;

	v = T->translator_set;
	T->translator_set_size = 0;
	while (v != NULL) {
		v = v->down;
		T->translator_set_size = T->translator_set_size + 1;
		};
	}

void SET_TEC_COVERED_SET(struct TEC_NODE *T, struct VECTOR_NODE *D, double MOE) {
	struct VECTOR_NODE *p, *d, *t;
	struct NUMBER_NODE *s;
	struct COV_NODE *c;

	if (T != NULL && D != NULL) {
		p = T->pattern;
		while (p != NULL) {
			t = T->translator_set;
			d = D;
			while (t != NULL) {
				s = VECTOR_PLUS(p->vector,t->vector);
				while (d != NULL && !VECTOR_EQUAL(d->vector,s,MOE))
					d = d->right;
				if (d == NULL) {
					PRINT_ERROR_MESSAGE("SET_TEC_COVERED_SET: Unable to find datapoint (d == NULL). Try using -m switch.");
					exit(1);
					};
				d->down = MAKE_NEW_VECTOR_NODE();
				s = DISPOSE_OF_NUMBER_NODE(s);
				t = t->down;
				};
			p = p->down;
			};
		d = D;
		c = NULL;
		while (d != NULL) {
			if (d->down != NULL) {
				if (c == NULL) {
					T->covered_set = MAKE_NEW_COV_NODE();
					c = T->covered_set;
					}
				else {
					c->next = MAKE_NEW_COV_NODE();
					c = c->next;
					};
				c->datapoint = d;
				d->down = DISPOSE_OF_VECTOR_NODE(d->down);
				};
			d = d->right;
			};
		c = NULL;
		}
	}

void SET_TEC_COVERAGE(struct TEC_NODE *T) {
	struct COV_NODE *c;

	c = T->covered_set;
	T->coverage = 0;
	while (c != NULL) {
		T->coverage = T->coverage + 1;
		c = c->next;
		}
	}

void SET_TEC_COMPRESSION_RATIO(struct TEC_NODE *T) {
	T->compression_ratio = ((double)(T->coverage))/((double)(T->pattern_size)+(double)(T->translator_set_size));
	}

void PRINT_VECTOR_SET(struct VECTOR_NODE *V, int DIRECTION, FILE *F) {
	struct VECTOR_NODE *v;

	v = V;
	while (v != NULL) {
		PRINT_VECTOR(v->vector,F);
		if (DIRECTION == DOWN)
			v = v->down;
		else
			v = v->right;
		};
	PRINT_NEW_LINE(F);
	}
	
double READ_NUMBER(FILE *F) {
	int c;
	double SIGN, VAL, POWER;

	c = READ_CHAR(F);
	if (c == '-')
		SIGN = -1;
	else
		SIGN = 1;
	if (c == '-' || c == '+')
		c = READ_CHAR(F);
	VAL = 0;
	while (IS_DIGIT(c)) {
		VAL = (10 * VAL) + (c - '0');
		c = READ_CHAR(F);
		};
	if (c == '.')
		c = READ_CHAR(F);
	POWER = 1;
	while (IS_DIGIT(c)) {
		VAL = (10 * VAL) + (c - '0');
		POWER = POWER * 10;
		c = READ_CHAR(F);
		};
	if (POWER > 1) REAL_OR_INT = REAL;
	return (SIGN * VAL / POWER);
	}

int VECTOR_LESS_THAN(struct NUMBER_NODE *v1, struct NUMBER_NODE *v2) {
	if (v1 == NULL)
		return (v2 != NULL);
	if (v2 == NULL)
		return FALSE;
	if (v1->number < v2->number)
		return TRUE;
	if (v1->number > v2->number)
		return FALSE;
	else
		return VECTOR_LESS_THAN(v1->next,v2->next);
	}

struct NUMBER_NODE *DISPOSE_OF_NUMBER_NODE(struct NUMBER_NODE *v) {
	if (v == NULL)
		return NULL;
	v->next = DISPOSE_OF_NUMBER_NODE(v->next);
	FREE(v);
	return NULL;
	}

struct NUMBER_NODE *VECTOR_MINUS(struct NUMBER_NODE *v1, struct NUMBER_NODE *v2) {
	struct NUMBER_NODE *p1, *p2, *v, *p;

	v = NULL;
	p1 = v1;
	p2 = v2;
	while (p1 != NULL && p2 != NULL) {
		if (v == NULL) {
			v = MAKE_NEW_NUMBER_NODE();
			p = v;
			}
		else {
			p->next = MAKE_NEW_NUMBER_NODE();
			p = p->next;
			};
		p->number = p1->number - p2->number;
		p1 = p1->next;
		p2 = p2->next;
		};
	p = p1 = p2 = NULL;
	return v;
	}

struct VECTOR_NODE *MERGE_VECTOR_COLUMNS(struct VECTOR_NODE *A, struct VECTOR_NODE *B) {
	struct VECTOR_NODE *a, *b, *C, *c;

	a = A->down;
	b = B->down;
	C = A;
	C->down = NULL;
	c = C;
	while (a != NULL && b != NULL) {
		if (VECTOR_LESS_THAN(b->right->vector,a->right->vector)) {
			c->down = b;
			b = b->down;
			}
		else {
			c->down = a;
			a = a->down;
			};
		c = c->down;
		c->down = NULL;
		};
	if (a == NULL)
		c->down = b;
	else
		c->down = a;
	a = b = c = NULL;
	return C;
	}

struct X_NODE *MERGE_PATTERN_ROWS(struct X_NODE *A, struct X_NODE *B) {
	struct X_NODE *a, *b, *C, *c;

	a = A;
	b = B;
	if (PATTERN_VEC_SEQ_LESS_THAN(b,a)) {
		C = b;
		b = b->right;
		}
	else {
		C = a;
		a = a->right;
		};
	C->right = NULL;
	c = C;
	while (a != NULL && b != NULL) {
		if (PATTERN_VEC_SEQ_LESS_THAN(b,a)) {
			c->right = b;
			b = b->right;
			}
		else {
			c->right = a;
			a = a->right;
			};
		c = c->right;
		c->right = NULL;
		};
	if (a == NULL)
		c->right = b;
	else
		c->right = a;
	a = b = c = NULL;
	return C;
	}

void PRINT_PATTERN(struct VECTOR_NODE *I, FILE *F) {
	struct VECTOR_NODE *p;

	p = I;
	while (p != NULL) {
		PRINT_VECTOR(p->right->vector,F);
		p = p->down;
		};
	PRINT_NEW_LINE(F);
	}

void PRINT_SET_OF_TRANSLATORS(struct VECTOR_NODE *I, FILE *F) {
	struct VECTOR_NODE *v, *J, *j2, *j1, *i;
	int FINISHED;

	v = j2 = j1 = i = NULL;
	if (I->down == NULL) {
		PRINT_VECTOR(I->right->down->vector,F);
		v = I->right->down->down;
		while (v != NULL) {
			PRINT_VECTOR(v->vector,F);
			v = v->down;
			};
		PRINT_NEW_LINE(F);
		}
	else {
		J = NULL;
		i = I;
		while (i != NULL) {
			if (J == NULL) {
				J = MAKE_NEW_VECTOR_NODE();
				j2 = J;
				}
			else {
				j2->down = MAKE_NEW_VECTOR_NODE();
				j2 = j2->down;
				};
			j2->right = i->right->down;
			j2->vector = MAKE_NEW_NUMBER_NODE();
			j2->vector->number = 1;
			i = i->down;
			};
		FINISHED = FALSE;
		j2 = J->down;
		j1 = J;
		while (!FINISHED) {
			while (j2->right != NULL && j2->vector->number <= j1->vector->number) {
				j2->right = j2->right->down;
				j2->vector->number = j2->vector->number + 1;
				};
			while (j2->right != NULL && VECTOR_LESS_THAN(j2->right->vector,j1->right->vector)) {
				j2->right = j2->right->down;
				j2->vector->number = j2->vector->number + 1;
				};
			if (j2->right == NULL)
				FINISHED = TRUE;
			else if (VECTOR_LESS_THAN(j1->right->vector,j2->right->vector)) {
				j1 = J;
				j2 = J->down;
				J->right = J->right->down;
				J->vector->number = J->vector->number + 1;
				}
			else if (j2->down == NULL) {
				PRINT_VECTOR(j2->right->vector,F);
				j2 = J;
				do {
					j2->right = j2->right->down;
					j2->vector->number = j2->vector->number + 1;
					j1 = j2;
					j2 = j2->down;
				} while (j2 != NULL);
				if (j1->right == NULL)
					FINISHED = TRUE;
				else {
					j1 = J;
					j2 = J->down;
					};
				}
			else {
				j1 = j2;
				j2 = j2->down;
				}
			};
		PRINT_NEW_LINE(F);
		j2 = NULL;
		j1 = J;
		while (j1 != NULL) {
			j1->right = NULL;
			j1 = j1->down;
			};
		J = DISPOSE_OF_VECTOR_NODE(J);
		};
	}

int PATTERN_VEC_SEQ_EQUAL(struct X_NODE *x1, struct X_NODE *x2, double MOE) {
	if (x1->size != x2->size)
		return FALSE;
	return VEC_LIST_EQUAL(x1->vec_seq,x2->vec_seq,DOWN,MOE);
	}
	
struct X_NODE *DISPOSE_OF_X_NODE(struct X_NODE *x) {
	if (x == NULL)
		return NULL;
	x->start_vec = NULL;
	x->vec_seq = DISPOSE_OF_VECTOR_NODE(x->vec_seq);
	x->down = DISPOSE_OF_X_NODE(x->down);
	x->right = DISPOSE_OF_X_NODE(x->right);
	FREE(x);
	return NULL;
	}

struct NUMBER_NODE *VECTOR_PLUS(struct NUMBER_NODE *v1, struct NUMBER_NODE *v2) {
	struct NUMBER_NODE *p1, *p2, *v, *p;

	v = NULL;
	p1 = v1;
	p2 = v2;
	while (p1 != NULL && p2 != NULL) {
		if (v == NULL) {
			v = MAKE_NEW_NUMBER_NODE();
			p = v;
			}
		else {
			p->next = MAKE_NEW_NUMBER_NODE();
			p = p->next;
			};
		p->number = p1->number + p2->number;
		p1 = p1->next;
		p2 = p2->next;
		};
	p = p1 = p2 = NULL;
	return v;
	}

void PRINT_VECTOR(struct NUMBER_NODE *v, FILE *F) {
	struct NUMBER_NODE *p;

	p = v;
	while (p != NULL) {
		PRINT_NUMBER(p->number,F);
		PRINT_CHAR(' ',F);
		p = p->next;
		};
	PRINT_NEW_LINE(F);
	}

int PATTERN_VEC_SEQ_LESS_THAN(struct X_NODE *x1, struct X_NODE *x2) {
	if (x1 == NULL || x2 == NULL) {
		PRINT_ERROR_MESSAGE("PATTERN_VEC_SEQ_LESS_THAN: Argument is NULL.");
		exit(1);
		};
	if (x1->size < x2->size)
		return TRUE;
	if (x1->size > x2->size)
		return FALSE;
	return VEC_LIST_LESS_THAN(x1->vec_seq, x2->vec_seq, DOWN);
	}

int VEC_LIST_EQUAL(struct VECTOR_NODE *v1, struct VECTOR_NODE *v2, int DIRECTION, double MOE) {
	if (v1 == NULL && v2 == NULL)
		return TRUE;
	if (v1 == NULL || v2 == NULL)
		return FALSE;
	if (!VECTOR_EQUAL(v1->vector,v2->vector,MOE))
		return FALSE;
	if (DIRECTION == DOWN)
		return VEC_LIST_EQUAL(v1->down,v2->down,DOWN,MOE);
	else
		return VEC_LIST_EQUAL(v1->right,v2->right,RIGHT,MOE);
	}

int VEC_LIST_LESS_THAN(struct VECTOR_NODE *v1, struct VECTOR_NODE *v2, int DIRECTION) {
	if (v1 == NULL)
		return (v2 != NULL);
	if (v2 == NULL)
		return FALSE;
	if (VECTOR_LESS_THAN(v1->vector,v2->vector))
		return TRUE;
	if (VECTOR_LESS_THAN(v2->vector,v1->vector))
		return FALSE;
	if (DIRECTION == DOWN)
		return VEC_LIST_LESS_THAN(v1->down,v2->down,DOWN);
	else
		return VEC_LIST_LESS_THAN(v1->down,v2->down,RIGHT);
	}

