//#include <malloc.h>
extern void *heap_limit;

int main(int argc, char const *argv[])
{
	int *addr1 = sf_malloc(128);
	printAddress(addr1);

	char *ptr = (char *)addr1;



	int a = 1;

	int *addr2 = sf_malloc(256);
	printAddress(addr2);

	sf_malloc(512);

	sf_malloc(512);

	sf_malloc(512);

	sf_free(addr2);
	//printf("After free, firstfree size is: %d\n", firstfree->size);
	sf_malloc(512);
	//printf("After free, firstfree->next size is: %d\n", firstfree->next->size);
	sf_malloc(512);
	printFreeList();

	while(ptr < (char *)heap_limit) {
		*ptr++ = 'a';
	}




	//Write("After free, firstfree->next size is: ", 50, 100);
	return 0;
}
